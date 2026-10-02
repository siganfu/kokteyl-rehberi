package com.kokteyl.rehberi.data.mapping

import com.kokteyl.rehberi.data.local.CocktailEntity
import com.kokteyl.rehberi.data.local.CocktailIngredientEntity

/** Hem yerel seed hem API verisi için ortak Cocktail + malzeme bağlantısı üretici. */
object CocktailBuilder {

    private val spiritGroups = setOf("Votka", "Cin", "Rom", "Tekila", "Viski", "Brendi")

    fun build(
        id: String,
        name: String,
        imageUrl: String?,
        category: String,
        alcoholic: Boolean,
        glassTr: String?,
        glassEn: String?,
        methodKey: String,
        garnishText: String?,
        instructionsTr: String,
        instructionsEn: String?,
        uses: List<IngredientUse>,
        curated: Boolean,
        extraTags: Collection<String>
    ): Pair<CocktailEntity, List<CocktailIngredientEntity>> {
        val key = TextUtils.nameKey(name)

        val mainSpirit: String? = if (!alcoholic) null else
            uses.firstOrNull { it.info.alcoholic && it.info.group in spiritGroups }?.info?.tr
                ?: uses.firstOrNull { it.info.alcoholic }?.info?.tr

        val garnish = garnishText
            ?: uses.filter { it.optional && !it.info.basic }
                .map { it.info.tr }.distinct().joinToString(", ").ifBlank { null }

        val requiredCount = uses.count { !it.optional }
        val shortInfo = listOfNotNull("$requiredCount malzeme", glassTr).joinToString(" · ")

        val tags = TagClassifier.classify(key, glassEn ?: glassTr, methodKey, uses, alcoholic, extraTags)

        val searchParts = mutableListOf(name, category)
        mainSpirit?.let { searchParts += it }
        glassTr?.let { searchParts += it }
        uses.forEach { searchParts += it.info.key; searchParts += it.info.tr }
        val searchText = " " + searchParts.joinToString(" ") { TextUtils.fold(it) }

        val entity = CocktailEntity(
            id = id,
            name = name,
            turkishName = name,
            nameKey = key,
            imageUrl = imageUrl,
            category = category,
            alcoholic = alcoholic,
            mainSpirit = mainSpirit,
            glass = glassTr,
            method = Translations.methodLabel(methodKey),
            garnish = garnish,
            instructions = instructionsTr,
            instructionsEn = instructionsEn,
            shortInfo = shortInfo,
            tags = tags,
            searchText = searchText,
            curated = curated,
            popularRank = Translations.popularRank(key)
        )
        val links = uses.mapIndexed { i, u ->
            CocktailIngredientEntity(
                cocktailId = id,
                ingredientId = u.info.key,
                position = i,
                amount = u.display,
                amountMl = u.ml,
                unit = u.unit,
                optional = u.optional
            )
        }
        return entity to links
    }
}

/** assets/cocktails_seed.json biçimi */
data class SeedIngredient(val i: String, val ml: Double?, val a: String?, val opt: Boolean?)

data class SeedCocktail(
    val name: String,
    val cat: String,
    val glass: String?,
    val method: String,
    val garnish: String?,
    val tags: List<String>?,
    val steps: List<String>,
    val ing: List<SeedIngredient>
)
