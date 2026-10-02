package com.kokteyl.rehberi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kokteyl.rehberi.data.local.CocktailSummary
import com.kokteyl.rehberi.data.repository.CocktailRepository
import com.kokteyl.rehberi.data.repository.SyncState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class HomeViewModel(private val repo: CocktailRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val popular: StateFlow<List<CocktailSummary>> = repo.observePopular()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val results: StateFlow<List<CocktailSummary>> = _query
        .debounce(200)
        .flatMapLatest { q -> if (q.isBlank()) flowOf(emptyList()) else repo.observeSummaries(q, "") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val selectedCount: StateFlow<Int> = repo.observeSelectedIds().map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val syncState: StateFlow<SyncState> = repo.syncState

    fun onQueryChange(q: String) {
        _query.value = q
    }

    fun toggleFavorite(id: String, favorite: Boolean) {
        viewModelScope.launch { repo.setFavorite(id, favorite) }
    }

    fun pickRandom(onResult: (String) -> Unit) {
        viewModelScope.launch { repo.randomCocktailId()?.let(onResult) }
    }
}
