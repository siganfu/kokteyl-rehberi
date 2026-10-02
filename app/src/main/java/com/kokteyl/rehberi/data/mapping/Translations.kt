package com.kokteyl.rehberi.data.mapping

/** Bardak, kategori ve hazırlama tekniği çevirileri + popüler/klasik kokteyl listeleri. */
object Translations {

    private val glassRules = listOf(
        "champagne flute" to "Şampanya flüt",
        "flute" to "Şampanya flüt",
        "champagne" to "Şampanya kadehi",
        "highball" to "Highball bardağı",
        "collins" to "Collins bardağı",
        "old-fashioned" to "Old Fashioned bardağı",
        "old fashioned" to "Old Fashioned bardağı",
        "whiskey sour" to "Viski (Sour) bardağı",
        "whiskey" to "Viski bardağı",
        "cocktail glass" to "Kokteyl (Martini) kadehi",
        "martini" to "Martini kadehi",
        "margarita" to "Margarita kadehi",
        "coupette" to "Margarita kadehi",
        "coupe" to "Coupe kadehi",
        "shot" to "Shot bardağı",
        "hurricane" to "Hurricane bardağı",
        "copper" to "Bakır kupa",
        "julep" to "Julep kupası",
        "wine" to "Şarap kadehi",
        "irish coffee" to "İrlanda kahvesi fincanı",
        "coffee" to "Kupa / Fincan",
        "beer" to "Bira bardağı",
        "pilsner" to "Bira bardağı",
        "punch" to "Punch kasesi",
        "pitcher" to "Sürahi",
        "pint" to "Pint bardağı",
        "snifter" to "Brendi kadehi",
        "brandy" to "Brendi kadehi",
        "cordial" to "Likör kadehi",
        "jar" to "Kavanoz",
        "nick" to "Nick & Nora kadehi",
        "balloon" to "Balon kadeh",
        "parfait" to "Parfe bardağı",
        "pousse" to "Pousse-café bardağı",
        "mug" to "Kupa",
        "cup" to "Fincan"
    )

    fun glass(en: String?): String? {
        val t = en?.trim()?.lowercase().orEmpty()
        if (t.isEmpty()) return null
        return glassRules.firstOrNull { t.contains(it.first) }?.second ?: "Bardak"
    }

    private val categories = mapOf(
        "ordinary drink" to "Klasik Kokteyl",
        "cocktail" to "Kokteyl",
        "shot" to "Shot",
        "punch / party drink" to "Parti İçkisi",
        "coffee / tea" to "Kahve ve Çay",
        "beer" to "Bira",
        "homemade liqueur" to "Ev Yapımı Likör",
        "soft drink" to "Alkolsüz İçecek",
        "milk / float / shake" to "Süt ve Milkshake",
        "cocoa" to "Kakao",
        "other / unknown" to "Diğer"
    )

    fun category(en: String?): String = categories[en?.trim()?.lowercase().orEmpty()] ?: "Kokteyl"

    /** Hazırlama teknikleri (anahtar -> Türkçe etiket) */
    val methods = mapOf(
        "shake" to "Çalkalama (Shake)",
        "dryshake" to "Kuru çalkalama (Dry Shake)",
        "stir" to "Karıştırma (Stir)",
        "build" to "Bardakta hazırlama (Build)",
        "muddle" to "Ezme (Muddle)",
        "blend" to "Blender (Blend)",
        "layer" to "Katmanlama (Layer)"
    )

    fun methodLabel(key: String): String = methods[key] ?: methods.getValue("build")

    /** Ana ekranda "Popüler" bölümünde sırayla gösterilecek kokteyller (nameKey). */
    val popular = listOf(
        "mojito", "margarita", "daiquiri", "moscowmule", "cosmopolitan", "longislandicedtea",
        "pinacolada", "sexonthebeach", "oldfashioned", "negroni", "martini", "whiskeysour",
        "tequilasunrise", "aperolspritz", "caipirinha", "manhattan", "espressomartini",
        "ginandtonic", "tomcollins", "bloodymary", "maitai", "cubalibre", "mimosa", "bellini"
    )

    fun popularRank(nameKey: String): Int {
        val i = popular.indexOf(nameKey)
        return if (i >= 0) i + 1 else 0
    }

    /** "Klasik" filtresi için ek klasikler */
    val classics: Set<String> = popular.toSet() + setOf(
        "sidecar", "gimlet", "kamikaze", "paloma", "amarettosour", "mintjulep", "whiterussian",
        "blackrussian", "screwdriver", "darkandstormy", "brandyalexander", "grasshopper", "rustynail",
        "godfather", "stinger", "americano", "boulevardier", "sazerac", "aviation", "bluelagoon",
        "singaporesling", "robroy", "harveywallbanger", "tequilaslammer", "irishcoffee", "margaritaclassic"
    )
}

object MethodDetector {
    private val blend = Regex("""\bblend""")
    private val layer = Regex("""\b(layer|float)""")

    /** İngilizce tarif metninden ana hazırlama tekniğini tahmin eder. */
    fun detect(instructionsEn: String?): String {
        val t = instructionsEn?.lowercase() ?: return "build"
        return when {
            "dry shake" in t -> "dryshake"
            blend.containsMatchIn(t) -> "blend"
            "shake" in t -> "shake"
            "stir" in t -> "stir"
            "muddle" in t -> "muddle"
            layer.containsMatchIn(t) -> "layer"
            else -> "build"
        }
    }
}
