package com.kokteyl.rehberi.ui.cocktails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kokteyl.rehberi.data.local.CocktailSummary
import com.kokteyl.rehberi.data.repository.CocktailRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Filtre etiketi: (ekranda görünen ad, veritabanındaki etiket) */
val COCKTAIL_FILTERS = listOf(
    "Tümü" to "",
    "Votka" to "votka",
    "Cin" to "cin",
    "Rom" to "rom",
    "Tekila" to "tekila",
    "Viski" to "viski",
    "Brendi" to "brendi",
    "Likör" to "likor",
    "Şampanya" to "sampanya",
    "Alkolsüz" to "alkolsuz",
    "Klasik" to "klasik",
    "Tiki" to "tiki",
    "Tropikal" to "tropikal",
    "Sour" to "sour",
    "Frozen" to "frozen",
    "Highball" to "highball",
    "Martini" to "martini"
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CocktailsViewModel(
    private val repo: CocktailRepository,
    initialTag: String
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _tag = MutableStateFlow(initialTag)
    val tag: StateFlow<String> = _tag

    /** null = henüz yüklenmedi */
    val results: StateFlow<List<CocktailSummary>?> =
        combine(_query.debounce(200), _tag) { q, t -> q to t }
            .flatMapLatest { (q, t) -> repo.observeSummaries(q, t) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun onQueryChange(q: String) {
        _query.value = q
    }

    fun onTagChange(t: String) {
        _tag.value = t
    }

    fun toggleFavorite(id: String, favorite: Boolean) {
        viewModelScope.launch { repo.setFavorite(id, favorite) }
    }
}
