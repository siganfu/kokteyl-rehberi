package com.kokteyl.rehberi.ui.settings

import androidx.lifecycle.ViewModel
import com.kokteyl.rehberi.AppContainer
import com.kokteyl.rehberi.data.repository.SyncState
import com.kokteyl.rehberi.data.repository.TranslateState
import com.kokteyl.rehberi.data.settings.ThemeMode
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val container: AppContainer) : ViewModel() {
    private val repo = container.repository

    val theme: StateFlow<ThemeMode> = container.settings.theme
    val syncState: StateFlow<SyncState> = repo.syncState
    val translateState: StateFlow<TranslateState> = repo.translateState

    fun lastSyncMillis(): Long = repo.lastSyncMillis

    fun setTheme(mode: ThemeMode) = container.settings.setTheme(mode)

    // Ekrandan çıkılsa bile devam etsin diye uygulama kapsamında çalıştırılır.
    fun sync() {
        container.appScope.launch { repo.syncFromApi() }
    }

    fun translateAll() {
        container.appScope.launch { repo.translatePending() }
    }
}
