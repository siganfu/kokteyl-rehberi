package com.kokteyl.rehberi.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kokteyl.rehberi.data.repository.CocktailRepository
import com.kokteyl.rehberi.domain.CocktailDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface TranslationUiState {
    data object Idle : TranslationUiState
    data object Working : TranslationUiState
    data object Failed : TranslationUiState
}

class DetailViewModel(
    private val repo: CocktailRepository,
    private val id: String
) : ViewModel() {

    val detail: StateFlow<CocktailDetail?> = repo.observeDetail(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _translation = MutableStateFlow<TranslationUiState>(TranslationUiState.Idle)
    val translation: StateFlow<TranslationUiState> = _translation

    fun toggleFavorite(favorite: Boolean) {
        viewModelScope.launch { repo.setFavorite(id, favorite) }
    }

    /** Tarif henüz Türkçeye çevrilmemişse cihaz üzerinde çevirir ve kaydeder. */
    fun ensureTranslated() {
        if (_translation.value == TranslationUiState.Working) return
        _translation.value = TranslationUiState.Working
        viewModelScope.launch {
            val ok = repo.translateInstructions(id)
            _translation.value = if (ok) TranslationUiState.Idle else TranslationUiState.Failed
        }
    }
}
