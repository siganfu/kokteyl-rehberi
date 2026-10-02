package com.kokteyl.rehberi.ui.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kokteyl.rehberi.data.repository.CocktailRepository
import com.kokteyl.rehberi.domain.MatchEngine
import com.kokteyl.rehberi.domain.MatchItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

data class ResultsState(
    val loading: Boolean = true,
    val selectedCount: Int = 0,
    val items: List<MatchItem> = emptyList()
)

class ResultsViewModel(repo: CocktailRepository) : ViewModel() {

    val state: StateFlow<ResultsState> = combine(
        repo.observeLinkRows(),
        repo.observeSelectedIds(),
        repo.observeIngredients(),
        repo.observeAllSummaries()
    ) { links, selected, ingredients, summaries ->
        val ingMap = ingredients.associateBy { it.id }
        val byId = summaries.associateBy { it.id }
        val items = MatchEngine.compute(links, selected, ingMap).mapNotNull { r ->
            byId[r.cocktailId]?.let { s ->
                MatchItem(
                    summary = s,
                    required = r.required,
                    have = r.have,
                    missingNames = r.missingIds.map { ingMap[it]?.turkishName ?: it }
                )
            }
        }
        ResultsState(loading = false, selectedCount = selected.size, items = items)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResultsState())
}
