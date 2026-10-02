package com.kokteyl.rehberi.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kokteyl.rehberi.data.local.CocktailSummary
import com.kokteyl.rehberi.data.repository.CocktailRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repo: CocktailRepository) : ViewModel() {

    /** null = yükleniyor */
    val favorites: StateFlow<List<CocktailSummary>?> = repo.observeFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun toggleFavorite(id: String, favorite: Boolean) {
        viewModelScope.launch { repo.setFavorite(id, favorite) }
    }
}
