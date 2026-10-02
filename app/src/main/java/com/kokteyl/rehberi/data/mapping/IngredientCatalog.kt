package com.kokteyl.rehberi.data.mapping

import com.kokteyl.rehberi.data.local.IngredientEntity
import java.util.Locale

data class IngredientInfo(
    val key: String,              // İngilizce kanonik anahtar (küçük harf), veritabanı id'si
    val tr: String,               // Türkçe görünen ad
    val category: String,         // Türkçe kategori adı
    val group: String? = null,    // Alkol grubu: Votka, Cin, Rom, Tekila, Viski, Brendi, Likör, ...
    val alcoholic: Boolean = false,
    val garnish: Boolean = false, // garnitür -> eşleştirmede zorunlu değil
    val generic: Boolean = false, // genel ad (örn. "Rom"); gruptaki her özel ürün karşılar
    val basic: Boolean = false,   // buz, su -> zorunlu sayılmaz
    val hidden: Boolean = false,  // seçim ekranında gösterilmez
    val satisfiedBy: Set<String> = emptySet(), // bu malzemeyi karşılayan diğer malzemeler (limon -> limon suyu)
    val aliases: List<String> = emptyList(),
    val dynamic: Boolean = false  // katalogda yok, API'den geldi (çevirisi bekliyor)
)

fun IngredientInfo.toEntity() = IngredientEntity(
    id = key,
    name = key,
    turkishName = tr,
    category = category,
    ingredientGroup = group,
    alcoholic = alcoholic,
    garnish = garnish,
    generic = generic,
    hidden = hidden,
    translated = !dynamic
)

object IngredientCatalog {
    val categoryOrder = listOf(
        "Alkollü",
        "Meyve Suları ve Püreler",
        "Şuruplar",
        "Gazlı İçecekler ve Mikserler",
        "Meyve ve Sebzeler",
        "Otlar ve Baharatlar",
        "Süt Ürünleri ve Yumurta",
        "Diğer"
    )

    val alcoholGroupOrder = listOf(
        "Votka", "Cin", "Rom", "Tekila", "Viski", "Brendi", "Likör",
        "Vermut", "Şampanya", "Şarap", "Bira", "Bitter"
    )

    private val categoryNames = mapOf(
        "A" to "Alkollü",
        "J" to "Meyve Suları ve Püreler",
        "S" to "Şuruplar",
        "M" to "Gazlı İçecekler ve Mikserler",
        "F" to "Meyve ve Sebzeler",
        "H" to "Otlar ve Baharatlar",
        "D" to "Süt Ürünleri ve Yumurta",
        "O" to "Diğer"
    )

    val all: List<IngredientInfo> by lazy { parse(IngredientData.TSV) }

    private val byKeyMap: Map<String, IngredientInfo> by lazy { all.associateBy { it.key } }

    private val index: Map<String, IngredientInfo> by lazy {
        val m = HashMap<String, IngredientInfo>()
        all.forEach { m[it.key] = it }
        all.forEach { info -> info.aliases.forEach { a -> if (!m.containsKey(a)) m[a] = info } }
        m
    }

    fun byKey(key: String): IngredientInfo? = byKeyMap[key]

    /** API'den gelen İngilizce malzeme adını katalogdaki kayda çevirir. Bulunamazsa null. */
    fun resolve(raw: String): IngredientInfo? {
        var n = raw.trim().lowercase(Locale.ROOT).replace(Regex("\\s+"), " ")
        if (n.isEmpty()) return null
        index[n]?.let { return it }
        n = n.removePrefix("fresh ").removePrefix("freshly squeezed ").removePrefix("fresh-squeezed ")
        index[n]?.let { return it }
        val folded = TextUtils.fold(n)
        index[folded]?.let { return it }
        if (folded.endsWith("es")) index[folded.dropLast(2)]?.let { return it }
        if (folded.endsWith("s")) index[folded.dropLast(1)]?.let { return it }
        return null
    }

    /** Katalogda olmayan malzeme için geçici kayıt (sonradan çevrilir). */
    fun dynamic(raw: String): IngredientInfo? {
        val key = TextUtils.slug(raw)
        if (key.isEmpty()) return null
        return IngredientInfo(
            key = key,
            tr = TextUtils.titleCase(raw),
            category = "Diğer",
            dynamic = true
        )
    }

    private fun String.splitList(): List<String> =
        if (isBlank()) emptyList()
        else split(";").map { it.trim().lowercase(Locale.ROOT) }.filter { it.isNotEmpty() }

    private fun parse(tsv: String): List<IngredientInfo> =
        tsv.lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .map { line ->
                val f = line.split("|")
                fun col(i: Int): String = f.getOrElse(i) { "" }.trim()
                val flags = col(4)
                IngredientInfo(
                    key = col(0),
                    tr = col(1),
                    category = categoryNames[col(2)] ?: "Diğer",
                    group = col(3).ifEmpty { null },
                    alcoholic = col(2) == "A" && 'n' !in flags,
                    garnish = 'g' in flags,
                    generic = 'x' in flags,
                    basic = 'b' in flags,
                    hidden = 'h' in flags,
                    satisfiedBy = col(5).splitList().toSet(),
                    aliases = col(6).splitList()
                )
            }
            .toList()
}
