package com.kokteyl.rehberi.data.mapping

/** Bir kokteylde kullanılan malzeme (ölçüsü ve zorunlu/opsiyonel bilgisiyle). */
data class IngredientUse(
    val info: IngredientInfo,
    val display: String,
    val ml: Double?,
    val unit: String?,
    val optional: Boolean
)

/** Filtre etiketlerini (votka, rom, klasik, tiki, sour, frozen, highball, martini, alkolsuz...) üretir. */
object TagClassifier {

    private val groupTags = mapOf(
        "Votka" to "votka", "Cin" to "cin", "Rom" to "rom", "Tekila" to "tekila",
        "Viski" to "viski", "Brendi" to "brendi", "Likör" to "likor", "Şampanya" to "sampanya"
    )

    private val tropicalKeys = setOf(
        "pineapple juice", "pineapple", "coconut", "cream of coconut", "coconut milk", "coconut rum",
        "passion fruit juice", "passion fruit puree", "passion fruit syrup", "passion fruit liqueur",
        "passion fruit", "mango juice", "mango puree", "mango", "banana", "creme de banana",
        "orgeat syrup", "falernum", "coconut syrup", "coconut water", "blue curacao"
    )
    private val tikiNames = setOf(
        "maitai", "zombie", "hurricane", "painkiller", "pinacolada", "bluehawaii", "scorpion",
        "jetpilot", "navygrog", "fogcutter"
    )
    private val sourNames = setOf("daiquiri", "margarita", "sidecar", "gimlet", "kamikaze", "amarettosour")
    private val sweeteners = setOf("simple syrup", "sugar", "triple sec", "honey syrup", "agave syrup", "sweet and sour")

    fun classify(
        nameKey: String,
        glassEn: String?,
        method: String,
        uses: List<IngredientUse>,
        alcoholic: Boolean,
        extra: Collection<String>
    ): String {
        val tags = linkedSetOf<String>()
        val keys = uses.map { it.info.key }.toSet()

        uses.forEach { u -> u.info.group?.let { g -> groupTags[g]?.let(tags::add) } }
        if (!alcoholic) tags += "alkolsuz"

        if (nameKey in Translations.classics) tags += "klasik"

        val glass = glassEn?.lowercase().orEmpty()
        if ("highball" in glass || "collins" in glass) tags += "highball"
        if ("martini" in glass || "cocktail glass" in glass || "martini" in nameKey) tags += "martini"

        if (method == "blend" || "frozen" in nameKey) tags += "frozen"

        val hasCitrus = "lemon juice" in keys || "lime juice" in keys
        val hasSweet = keys.any { it in sweeteners }
        val hasSpirit = uses.any { it.info.alcoholic && it.info.group != null }
        if ("sour" in nameKey || nameKey in sourNames ||
            (hasCitrus && hasSweet && hasSpirit && (method == "shake" || method == "dryshake"))
        ) tags += "sour"

        val tropical = keys.any { it in tropicalKeys }
        if (tropical) tags += "tropikal"
        if (nameKey in tikiNames || (tropical && "rom" in tags)) tags += "tiki"

        extra.forEach { tags += TextUtils.fold(it) }
        return tags.joinToString(" ")
    }
}
