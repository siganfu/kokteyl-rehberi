package com.kokteyl.rehberi.domain

import com.kokteyl.rehberi.data.local.CocktailEntity
import com.kokteyl.rehberi.data.local.CocktailSummary
import com.kokteyl.rehberi.data.local.IngredientEntity
import com.kokteyl.rehberi.data.local.LinkRow
import com.kokteyl.rehberi.data.mapping.IngredientCatalog

data class IngredientLine(
    val id: String,
    val name: String,
    val amount: String,
    val optional: Boolean,
    val owned: Boolean
)

data class CocktailDetail(
    val cocktail: CocktailEntity,
    val lines: List<IngredientLine>,
    val steps: List<String>
)

data class MatchResult(
    val cocktailId: String,
    val required: Int,
    val have: Int,
    val missingIds: List<String>
) {
    val missing: Int get() = required - have
    val percent: Int get() = if (required == 0) 0 else (have * 100) / required
}

data class MatchItem(
    val summary: CocktailSummary,
    val required: Int,
    val have: Int,
    val missingNames: List<String>
) {
    val missing: Int get() = required - have
    val percent: Int get() = if (required == 0) 0 else (have * 100) / required
}

/**
 * Malzeme eşleştirme algoritması.
 *  - Garnitür / buz / su gibi "opsiyonel" malzemeler zorunlu sayılmaz.
 *  - Genel malzeme ("Viski") aynı gruptaki herhangi bir özel ürünle (Burbon, Scotch...) karşılanır.
 *  - Limon -> limon suyu gibi katalogdaki "satisfiedBy" kuralları uygulanır.
 *  - Hiçbir malzemesi eşleşmeyen kokteyller listelenmez.
 * Sıralama: eksik sayısı (artan) -> yüzde (azalan) -> mevcut malzeme sayısı (azalan).
 */
object MatchEngine {

    fun compute(
        links: List<LinkRow>,
        selected: Set<String>,
        ingredients: Map<String, IngredientEntity>
    ): List<MatchResult> {
        if (selected.isEmpty()) return emptyList()
        val selectedGroups = selected.mapNotNull { ingredients[it]?.ingredientGroup }.toSet()
        val out = ArrayList<MatchResult>()

        for ((cocktailId, rows) in links.groupBy { it.cocktailId }) {
            val required = rows.filter { !it.optional }.map { it.ingredientId }.distinct()
            if (required.isEmpty()) continue
            val missing = required.filterNot { isSatisfied(it, selected, selectedGroups, ingredients) }
            val have = required.size - missing.size
            if (have == 0) continue
            out += MatchResult(cocktailId, required.size, have, missing)
        }
        return out.sortedWith(
            compareBy<MatchResult> { it.missing }
                .thenByDescending { it.percent }
                .thenByDescending { it.have }
        )
    }

    private fun isSatisfied(
        id: String,
        selected: Set<String>,
        selectedGroups: Set<String>,
        ingredients: Map<String, IngredientEntity>
    ): Boolean {
        if (id in selected) return true
        val info = IngredientCatalog.byKey(id)
        if (info != null && info.satisfiedBy.any { it in selected }) return true
        val ing = ingredients[id]
        val group = ing?.ingredientGroup
        if (ing != null && ing.generic && group != null && group in selectedGroups) return true
        return false
    }
}
