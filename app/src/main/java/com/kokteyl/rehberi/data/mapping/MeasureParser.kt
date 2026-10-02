package com.kokteyl.rehberi.data.mapping

import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * @param display   ekranda gösterilen Türkçe ölçü metni ("50 ml", "2 çay kaşığı", "Üstünü tamamla")
 * @param ml        mililitre karşılığı (biliniyorsa)
 * @param unit      Türkçe birim ("ml", "dash", "yaprak" ...)
 * @param garnishUnit dilim/dal/kabuk gibi garnitür birimi mi
 */
data class Measure(
    val display: String,
    val ml: Double?,
    val unit: String?,
    val garnishUnit: Boolean
)

/** TheCocktailDB ölçülerini ("1 1/2 oz", "2 cl", "1 dash", "Juice of 1/2") Türkçe ve ml'ye çevirir. */
object MeasureParser {

    /** 1 oz = 30 ml (bar standardı). Ons'tan çevrilen değerler 5 ml'ye yuvarlanır. */
    const val OZ_ML = 30.0

    private const val NUM = """(?:\d+\s+\d+/\d+|\d+/\d+|\d+(?:[.,]\d+)?)"""
    private val qtyRegex = Regex("""^\s*($NUM)(?:\s*(?:-|–|to)\s*($NUM))?\s*(.*)$""", RegexOption.IGNORE_CASE)
    private val juiceOfRegex = Regex("""^\s*juice of\s*($NUM)?.*$""", RegexOption.IGNORE_CASE)
    private val topRegex = Regex("""\b(top|fill)\b""")
    private val rimRegex = Regex("""\brim\b""")

    private val mlFactor = mapOf(
        "ml" to 1.0, "milliliter" to 1.0, "millilitre" to 1.0,
        "cl" to 10.0, "centiliter" to 10.0, "centilitre" to 10.0,
        "dl" to 100.0,
        "l" to 1000.0, "liter" to 1000.0, "litre" to 1000.0,
        "oz" to OZ_ML, "ounce" to OZ_ML,
        "shot" to 45.0, "jigger" to 45.0
    )
    private val roundedUnits = setOf("oz", "ounce", "shot", "jigger")

    private val textUnits = mapOf(
        "tsp" to "çay kaşığı", "tspn" to "çay kaşığı", "teaspoon" to "çay kaşığı",
        "tbsp" to "yemek kaşığı", "tblsp" to "yemek kaşığı", "tbs" to "yemek kaşığı", "tablespoon" to "yemek kaşığı",
        "dash" to "dash", "splash" to "sıçrama", "drop" to "damla", "pinch" to "tutam",
        "slice" to "dilim", "wedge" to "dilim",
        "leaf" to "yaprak", "leaves" to "yaprak",
        "sprig" to "dal", "twist" to "kabuk", "peel" to "kabuk",
        "cube" to "küp", "part" to "ölçü", "measure" to "ölçü", "piece" to "parça",
        "can" to "kutu", "bottle" to "şişe", "glass" to "bardak", "cup" to "su bardağı",
        "scoop" to "top", "whole" to "adet", "stick" to "çubuk", "spoon" to "kaşık"
    )
    private val garnishUnits = setOf("slice", "wedge", "twist", "peel", "sprig")

    fun parse(raw: String?): Measure {
        val text = raw?.trim().orEmpty()
        if (text.isEmpty()) return Measure("", null, null, false)

        val m = qtyRegex.find(text)
        if (m == null) return parseWithoutNumber(text)

        val v1 = toNumber(m.groupValues[1])
        val v2 = m.groupValues[2].takeIf { it.isNotEmpty() }?.let { toNumber(it) }
        val restWords = m.groupValues[3].trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.toMutableList()
        if (restWords.size >= 2 && restWords[0].lowercase().trimEnd('.') == "fl" &&
            restWords[1].lowercase().trimEnd('.') == "oz"
        ) restWords.removeAt(0)
        val token = restWords.firstOrNull()?.lowercase()?.trim('.', ',', '(', ')').orEmpty()

        val factor = lookup(mlFactor, token)
        if (factor != null) {
            val nice = lookupKey(mlFactor, token) in roundedUnits
            val a = fmtMl(v1 * factor, nice)
            val display = if (v2 != null) "$a–${fmtMl(v2 * factor, nice)} ml" else "$a ml"
            return Measure(display, v1 * factor, "ml", false)
        }

        val tr = lookup(textUnits, token)
        if (tr != null) {
            val key = lookupKey(textUnits, token)
            val qty = if (v2 != null) "${fmtQty(v1)}–${fmtQty(v2)}" else fmtQty(v1)
            return Measure("$qty $tr", null, tr, key in garnishUnits)
        }

        val qty = if (v2 != null) "${fmtQty(v1)}–${fmtQty(v2)}" else fmtQty(v1)
        return Measure("$qty adet", null, "adet", false)
    }

    private fun parseWithoutNumber(text: String): Measure {
        val lower = text.lowercase()
        juiceOfRegex.find(text)?.let { j ->
            val q = j.groupValues[1].takeIf { it.isNotEmpty() }?.let { toNumber(it) } ?: 1.0
            return Measure("${fmtQty(q)} adet (suyu)", null, "adet", false)
        }
        return when {
            topRegex.containsMatchIn(lower) -> Measure("Üstünü tamamla", null, null, false)
            "taste" in lower -> Measure("Damak tadına göre", null, null, false)
            "garnish" in lower -> Measure("Süsleme için", null, null, true)
            rimRegex.containsMatchIn(lower) -> Measure("Bardak kenarı için", null, null, true)
            "splash" in lower -> Measure("1 sıçrama", null, "sıçrama", false)
            "dash" in lower -> Measure("1 dash", null, "dash", false)
            "pinch" in lower -> Measure("1 tutam", null, "tutam", false)
            "drop" in lower -> Measure("1 damla", null, "damla", false)
            "float" in lower -> Measure("Üstüne yüzdür", null, null, false)
            "dust" in lower || "sprinkle" in lower -> Measure("Serpmek için", null, null, true)
            "few" in lower || "some" in lower || "needed" in lower || "extra" in lower || "more" in lower ->
                Measure("Yeterince", null, null, false)
            else -> Measure(text, null, null, false)
        }
    }

    /** ml'yi görüntü için biçimler. nice=true ise (onstan çevrilenler) 5 ml'ye yuvarlar. */
    fun fmtMl(v: Double, nice: Boolean = false): String {
        val r = when {
            nice && v >= 10 -> Math.round(v / 5.0) * 5.0
            nice -> Math.round(v).toDouble()
            else -> (v * 10).roundToInt() / 10.0
        }
        return if (r % 1.0 == 0.0) r.toLong().toString() else r.toString().replace('.', ',')
    }

    private fun fmtQty(v: Double): String {
        val whole = v.toInt()
        val frac = v - whole
        val fracStr: String? = when {
            frac < 0.05 -> ""
            abs(frac - 0.25) < 0.05 -> "1/4"
            abs(frac - 0.5) < 0.05 -> "1/2"
            abs(frac - 0.75) < 0.05 -> "3/4"
            abs(frac - 0.333) < 0.05 -> "1/3"
            abs(frac - 0.667) < 0.05 -> "2/3"
            else -> null
        }
        return when {
            fracStr == null -> ((v * 10).roundToInt() / 10.0).toString().replace('.', ',').removeSuffix(",0")
            fracStr.isEmpty() -> whole.toString()
            whole == 0 -> fracStr
            else -> "$whole $fracStr"
        }
    }

    private fun toNumber(s: String): Double {
        var sum = 0.0
        for (part in s.trim().replace(',', '.').split(Regex("\\s+"))) {
            sum += if ("/" in part) {
                val p = part.split("/")
                p[0].toDouble() / p[1].toDouble()
            } else part.toDouble()
        }
        return sum
    }

    private fun <T> lookup(map: Map<String, T>, token: String): T? = map[lookupKey(map, token)]

    private fun <T> lookupKey(map: Map<String, T>, token: String): String? = when {
        token.isEmpty() -> null
        map.containsKey(token) -> token
        map.containsKey(token.removeSuffix("es")) -> token.removeSuffix("es")
        map.containsKey(token.removeSuffix("s")) -> token.removeSuffix("s")
        else -> null
    }
}
