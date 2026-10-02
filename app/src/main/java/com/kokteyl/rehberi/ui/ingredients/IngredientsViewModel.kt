package com.kokteyl.rehberi.ui.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kokteyl.rehberi.data.local.IngredientEntity
import com.kokteyl.rehberi.data.mapping.IngredientCatalog
import com.kokteyl.rehberi.data.mapping.TextUtils
import com.kokteyl.rehberi.data.repository.CocktailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PickerSection(val title: String, val items: List<IngredientEntity>)

class IngredientsViewModel(private val repo: CocktailRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val selected: StateFlow<Set<String>> = repo.observeSelectedIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val sections: StateFlow<List<PickerSection>> =
        combine(repo.observePickerIngredients(), _query) { list, q -> buildSections(list, q) }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onQueryChange(q: String) {
        _query.value = q
    }

    fun toggle(id: String, checked: Boolean) {
        viewModelScope.launch { repo.setSelected(id, checked) }
    }

    fun clear() {
        viewModelScope.launch { repo.clearSelection() }
    }

    private fun buildSections(list: List<IngredientEntity>, query: String): List<PickerSection> {
        val f = TextUtils.fold(query.trim())
        val filtered = if (f.isEmpty()) list else list.filter {
            TextUtils.fold(it.turkishName).contains(f) || TextUtils.fold(it.name).contains(f)
        }
        val out = mutableListOf<PickerSection>()
        for (cat in IngredientCatalog.categoryOrder) {
            val inCat = filtered.filter { it.category == cat }.sortedBy { TextUtils.fold(it.turkishName) }
            if (inCat.isEmpty()) continue
            if (cat == "Alkollü") {
                for (g in IngredientCatalog.alcoholGroupOrder) {
                    val items = inCat.filter { it.ingredientGroup == g }
                    if (items.isNotEmpty()) out += PickerSection("Alkollü · $g", items)
                }
                val rest = inCat.filter { it.ingredientGroup == null || it.ingredientGroup !in IngredientCatalog.alcoholGroupOrder }
                if (rest.isNotEmpty()) out += PickerSection("Alkollü · Diğer", rest)
            } else {
                out += PickerSection(cat, inCat)
            }
        }
        return out
    }
}
