package com.kokteyl.rehberi.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.room.withTransaction
import com.google.gson.Gson
import com.kokteyl.rehberi.data.local.AppDatabase
import com.kokteyl.rehberi.data.local.CocktailSummary
import com.kokteyl.rehberi.data.local.IngredientEntity
import com.kokteyl.rehberi.data.local.LinkRow
import com.kokteyl.rehberi.data.local.UserIngredientEntity
import com.kokteyl.rehberi.data.mapping.CocktailBuilder
import com.kokteyl.rehberi.data.mapping.IngredientCatalog
import com.kokteyl.rehberi.data.mapping.IngredientUse
import com.kokteyl.rehberi.data.mapping.MeasureParser
import com.kokteyl.rehberi.data.mapping.MethodDetector
import com.kokteyl.rehberi.data.mapping.SeedCocktail
import com.kokteyl.rehberi.data.mapping.TextUtils
import com.kokteyl.rehberi.data.mapping.Translations
import com.kokteyl.rehberi.data.mapping.toEntity
import com.kokteyl.rehberi.data.remote.CocktailApi
import com.kokteyl.rehberi.data.settings.SettingsStore
import com.kokteyl.rehberi.domain.CocktailDetail
import com.kokteyl.rehberi.domain.IngredientLine
import com.kokteyl.rehberi.translate.TextTranslator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException

sealed interface SyncState {
    data object Idle : SyncState
    data class Running(val done: Int, val total: Int, val message: String) : SyncState
    data class Success(val processed: Int, val total: Int) : SyncState
    data class Failed(val message: String) : SyncState
}

sealed interface TranslateState {
    data object Idle : TranslateState
    data class Running(val done: Int, val total: Int) : TranslateState
    data object Done : TranslateState
    data class Failed(val message: String) : TranslateState
}

class CocktailRepository(
    private val context: Context,
    private val db: AppDatabase,
    private val api: CocktailApi,
    private val translator: TextTranslator,
    private val settings: SettingsStore
) {
    private val cocktailDao = db.cocktailDao()
    private val ingredientDao = db.ingredientDao()

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _translateState = MutableStateFlow<TranslateState>(TranslateState.Idle)
    val translateState: StateFlow<TranslateState> = _translateState.asStateFlow()

    private val syncMutex = Mutex()
    private val translateMutex = Mutex()

    val lastSyncMillis: Long get() = settings.lastSync

    // ---------------------------------------------------------------- başlangıç

    /** Uygulama açılışı: yerel tohum verisini yükle; ilk açılışta internet varsa API'den indir. */
    suspend fun initialize() {
        try {
            if (cocktailDao.count() == 0) {
                settings.seedVersion = 0
                settings.lastSync = 0L
            }
            seedIfNeeded()
            if (settings.lastSync == 0L) {
                if (isOnline()) syncFromApi() else _syncState.value = SyncState.Failed(OFFLINE_MESSAGE)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "initialize failed", e)
        }
    }

    private suspend fun seedIfNeeded() = withContext(Dispatchers.IO) {
        if (settings.seedVersion >= SEED_VERSION) return@withContext
        ingredientDao.insertAll(IngredientCatalog.all.map { it.toEntity() })
        val json = context.assets.open("cocktails_seed.json").bufferedReader().use { it.readText() }
        val seed = Gson().fromJson(json, Array<SeedCocktail>::class.java).toList()
        db.withTransaction { seed.forEach { importSeed(it) } }
        settings.seedVersion = SEED_VERSION
    }

    private suspend fun importSeed(s: SeedCocktail) {
        val uses = s.ing.mapNotNull { si ->
            val info = IngredientCatalog.byKey(si.i) ?: return@mapNotNull null
            val display = si.a ?: si.ml?.let { "${MeasureParser.fmtMl(it)} ml" }.orEmpty()
            IngredientUse(
                info = info,
                display = display,
                ml = si.ml,
                unit = if (si.ml != null) "ml" else null,
                optional = si.opt == true || info.garnish || info.basic
            )
        }
        val alcoholic = uses.any { it.info.alcoholic }
        val key = TextUtils.nameKey(s.name)
        val existing = cocktailDao.findByKey(key)
        val (entity, links) = CocktailBuilder.build(
            id = existing?.id ?: "seed_$key",
            name = s.name,
            imageUrl = existing?.imageUrl,
            category = s.cat,
            alcoholic = alcoholic,
            glassTr = s.glass,
            glassEn = null,
            methodKey = s.method,
            garnishText = s.garnish,
            instructionsTr = s.steps.joinToString("\n"),
            instructionsEn = existing?.instructionsEn,
            uses = uses,
            curated = true,
            extraTags = s.tags.orEmpty()
        )
        saveCocktail(entity, links, existing?.favorite ?: false, existing != null)
    }

    private suspend fun saveCocktail(
        entity: com.kokteyl.rehberi.data.local.CocktailEntity,
        links: List<com.kokteyl.rehberi.data.local.CocktailIngredientEntity>,
        favorite: Boolean,
        exists: Boolean
    ) {
        if (!exists) {
            cocktailDao.insert(entity)
        } else {
            cocktailDao.update(entity.copy(favorite = favorite))
            cocktailDao.deleteLinks(entity.id)
        }
        cocktailDao.insertLinks(links)
    }

    // ---------------------------------------------------------------- okuma

    fun observeSummaries(query: String, tag: String): Flow<List<CocktailSummary>> =
        cocktailDao.observeSummaries(TextUtils.sanitizeQuery(query), tag)

    fun observePopular() = cocktailDao.observePopular()
    fun observeFavorites() = cocktailDao.observeFavorites()
    fun observeAllSummaries() = cocktailDao.observeAllSummaries()
    fun observeLinkRows(): Flow<List<LinkRow>> = cocktailDao.observeLinkRows()
    fun observeIngredients(): Flow<List<IngredientEntity>> = ingredientDao.observeAll()
    fun observePickerIngredients(): Flow<List<IngredientEntity>> = ingredientDao.observePicker()
    fun observeSelectedIds(): Flow<Set<String>> = ingredientDao.observeSelected().map { it.toSet() }

    suspend fun setSelected(id: String, selected: Boolean) {
        if (selected) ingredientDao.select(UserIngredientEntity(id)) else ingredientDao.deselect(id)
    }

    suspend fun clearSelection() = ingredientDao.clearSelection()
    suspend fun setFavorite(id: String, favorite: Boolean) = cocktailDao.setFavorite(id, favorite)
    suspend fun randomCocktailId(): String? = cocktailDao.randomId()

    fun observeDetail(id: String): Flow<CocktailDetail?> = combine(
        cocktailDao.observe(id),
        cocktailDao.observeLinks(id),
        ingredientDao.observeAll(),
        ingredientDao.observeSelected()
    ) { cocktail, links, ingredients, selected ->
        if (cocktail == null) {
            null
        } else {
            val map = ingredients.associateBy { it.id }
            val sel = selected.toSet()
            CocktailDetail(
                cocktail = cocktail,
                lines = links.map { l ->
                    IngredientLine(
                        id = l.ingredientId,
                        name = map[l.ingredientId]?.turkishName ?: l.ingredientId,
                        amount = l.amount,
                        optional = l.optional,
                        owned = l.ingredientId in sel
                    )
                },
                steps = cocktail.instructions.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
            )
        }
    }.flowOn(Dispatchers.Default)

    // ---------------------------------------------------------------- API senkronizasyonu

    fun isOnline(): Boolean {
        val cm = context.getSystemService(ConnectivityManager::class.java) ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /** TheCocktailDB'den kokteylleri indirir/günceller. Mevcut veri, favoriler ve seçimler korunur. */
    suspend fun syncFromApi() {
        syncMutex.withLock { doSync() }
    }

    private suspend fun doSync() {
        if (!isOnline()) {
            _syncState.value = SyncState.Failed(OFFLINE_MESSAGE)
            return
        }
        withContext(Dispatchers.IO) {
            try {
                val letters = ('a'..'z').map { it.toString() } + ('0'..'9').map { it.toString() }
                var processed = 0
                letters.forEachIndexed { index, letter ->
                    _syncState.value = SyncState.Running(index, letters.size, "Kokteyller indiriliyor…")
                    val drinks = api.searchByFirstLetter(letter).drinks.orEmpty()
                    db.withTransaction { drinks.forEach { saveApiDrink(it) } }
                    processed += drinks.size
                }
                settings.lastSync = System.currentTimeMillis()
                _syncState.value = SyncState.Success(processed, cocktailDao.count())
                translateNewIngredientNames()
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                Log.w(TAG, "sync network error", e)
                _syncState.value = SyncState.Failed(OFFLINE_MESSAGE)
            } catch (e: Exception) {
                Log.e(TAG, "sync failed", e)
                _syncState.value = SyncState.Failed(GENERIC_ERROR)
            }
        }
    }

    private suspend fun saveApiDrink(d: Map<String, String?>) {
        val apiId = d["idDrink"]?.trim().orEmpty()
        val name = d["strDrink"]?.trim().orEmpty()
        if (apiId.isEmpty() || name.isEmpty()) return

        val key = TextUtils.nameKey(name)
        val thumb = d["strDrinkThumb"]?.trim()?.takeIf { it.isNotEmpty() }
        val existing = cocktailDao.findByKey(key)

        // Elle hazırladığımız Türkçe tarifleri bozma; yalnızca eksik görseli tamamla.
        if (existing != null && existing.curated) {
            if (thumb != null && existing.imageUrl != thumb) cocktailDao.setImage(existing.id, thumb)
            return
        }

        val uses = drinkToUses(d)
        if (uses.isEmpty()) return
        ingredientDao.insertAll(uses.map { it.info.toEntity() })

        val en = d["strInstructions"]?.trim()?.takeIf { it.isNotEmpty() }
        val trInstructions = if (existing != null && existing.instructionsEn == en) existing.instructions else ""
        val alcoholic = when (d["strAlcoholic"]?.trim()?.lowercase()) {
            "alcoholic" -> true
            "non alcoholic" -> false
            else -> uses.any { it.info.alcoholic }
        }
        val glassEn = d["strGlass"]?.trim()

        val (entity, links) = CocktailBuilder.build(
            id = existing?.id ?: "cdb_$apiId",
            name = name,
            imageUrl = thumb ?: existing?.imageUrl,
            category = Translations.category(d["strCategory"]),
            alcoholic = alcoholic,
            glassTr = Translations.glass(glassEn),
            glassEn = glassEn,
            methodKey = MethodDetector.detect(en),
            garnishText = null,
            instructionsTr = trInstructions,
            instructionsEn = en,
            uses = uses,
            curated = false,
            extraTags = emptyList()
        )
        saveCocktail(entity, links, existing?.favorite ?: false, existing != null)
    }

    private fun drinkToUses(d: Map<String, String?>): List<IngredientUse> {
        val result = mutableListOf<IngredientUse>()
        for (i in 1..15) {
            val raw = d["strIngredient$i"]?.trim().orEmpty()
            if (raw.isEmpty()) continue
            val info = IngredientCatalog.resolve(raw) ?: IngredientCatalog.dynamic(raw) ?: continue
            val m = MeasureParser.parse(d["strMeasure$i"])
            result += IngredientUse(
                info = info,
                display = m.display,
                ml = m.ml,
                unit = m.unit,
                optional = info.garnish || info.basic || m.garnishUnit
            )
        }
        return result
    }

    // ---------------------------------------------------------------- Türkçe çeviri

    /** Tek bir kokteylin İngilizce tarifini Türkçeye çevirip kaydeder. Başarılıysa true. */
    suspend fun translateInstructions(id: String, wifiOnly: Boolean = false): Boolean {
        val c = cocktailDao.get(id) ?: return false
        if (c.instructions.isNotBlank()) return true
        val en = c.instructionsEn?.takeIf { it.isNotBlank() } ?: return false
        val sentences = en.split(Regex("(?<=[.!?])\\s+")).map { it.trim() }.filter { it.isNotEmpty() }
        val out = mutableListOf<String>()
        for (s in sentences) {
            val r = translator.translate(s, wifiOnly).getOrNull() ?: return false
            out += r.trim()
        }
        if (out.isEmpty()) return false
        cocktailDao.setInstructions(id, out.joinToString("\n"))
        return true
    }

    /** Katalogda olmayan malzeme adlarını (yalnızca Wi-Fi'da, sessizce) çevirir. */
    private suspend fun translateNewIngredientNames() {
        try {
            for (ing in ingredientDao.pendingTranslation()) {
                val r = translator.translate(ing.name, wifiOnly = true).getOrNull() ?: return
                ingredientDao.update(ing.copy(turkishName = TextUtils.titleCase(r), translated = true))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "ingredient translation skipped", e)
        }
    }

    /** Ayarlar > "Tarifleri Türkçeye çevir": çevrilmemiş tüm tarifleri ve malzeme adlarını çevirir. */
    suspend fun translatePending() {
        if (translateMutex.isLocked) return
        translateMutex.withLock { doTranslatePending() }
    }

    private suspend fun doTranslatePending() = withContext(Dispatchers.IO) {
        try {
            val ingredients = ingredientDao.pendingTranslation()
            val ids = cocktailDao.pendingTranslationIds()
            val total = ingredients.size + ids.size
            if (total == 0) {
                _translateState.value = TranslateState.Done
                return@withContext
            }
            _translateState.value = TranslateState.Running(0, total)
            // Model hazır mı / internet var mı diye küçük bir deneme
            if (translator.translate("Hello").isFailure) {
                _translateState.value = TranslateState.Failed(
                    "Çeviri modeli indirilemedi. İnternet bağlantınızı kontrol edip tekrar deneyin."
                )
                return@withContext
            }
            var done = 0
            for (ing in ingredients) {
                translator.translate(ing.name).getOrNull()?.let {
                    ingredientDao.update(ing.copy(turkishName = TextUtils.titleCase(it), translated = true))
                }
                done++
                _translateState.value = TranslateState.Running(done, total)
            }
            for (id in ids) {
                translateInstructions(id)
                done++
                _translateState.value = TranslateState.Running(done, total)
            }
            _translateState.value = TranslateState.Done
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "translatePending failed", e)
            _translateState.value = TranslateState.Failed("Çeviri sırasında bir sorun oluştu.")
        }
    }

    companion object {
        private const val TAG = "CocktailRepository"
        const val SEED_VERSION = 1
        const val OFFLINE_MESSAGE = "İnternet bağlantısı bulunamadı. Kayıtlı tarifler kullanılabilir."
        private const val GENERIC_ERROR = "Güncelleme sırasında bir sorun oluştu. Kayıtlı tarifler kullanılabilir."
    }
}
