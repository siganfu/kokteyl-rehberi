package com.kokteyl.rehberi.data.mapping

import java.text.Normalizer
import java.util.Locale

object TextUtils {
    val TR: Locale = Locale.forLanguageTag("tr-TR")
    private val marks = Regex("\\p{Mn}+")
    private val nonSlug = Regex("[^a-z0-9]+")

    /** Küçük harfe çevirir, aksan/ş/ç/ğ/ö/ü/ı gibi işaretleri temizler. Arama ve eşleştirme için. */
    fun fold(s: String): String =
        Normalizer.normalize(s, Normalizer.Form.NFD)
            .replace(marks, "")
            .lowercase(Locale.ROOT)
            .replace('ı', 'i')

    /** "Piña Colada" -> "pinacolada". Aynı kokteylin farklı yazımlarını eşleştirmek için. */
    fun nameKey(s: String): String = fold(s).filter { it.isLetterOrDigit() }

    fun slug(s: String): String = fold(s).replace(nonSlug, "-").trim('-')

    fun upperTr(s: String): String = s.uppercase(TR)

    fun titleCase(s: String): String =
        s.trim().split(" ").filter { it.isNotEmpty() }
            .joinToString(" ") { w -> w.replaceFirstChar { c -> c.uppercase() } }

    fun sanitizeQuery(q: String): String =
        fold(q).filter { it.isLetterOrDigit() || it == ' ' }.trim()
}
