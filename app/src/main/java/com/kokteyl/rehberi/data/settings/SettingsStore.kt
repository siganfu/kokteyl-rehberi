package com.kokteyl.rehberi.data.settings

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode { DARK, LIGHT, SYSTEM }

class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("kokteyl_settings", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(readTheme())
    val theme: StateFlow<ThemeMode> = _theme.asStateFlow()

    fun setTheme(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME, mode.name).apply()
        _theme.value = mode
    }

    var lastSync: Long
        get() = prefs.getLong(KEY_LAST_SYNC, 0L)
        set(value) {
            prefs.edit().putLong(KEY_LAST_SYNC, value).apply()
        }

    var seedVersion: Int
        get() = prefs.getInt(KEY_SEED_VERSION, 0)
        set(value) {
            prefs.edit().putInt(KEY_SEED_VERSION, value).apply()
        }

    private fun readTheme(): ThemeMode =
        runCatching { ThemeMode.valueOf(prefs.getString(KEY_THEME, ThemeMode.DARK.name) ?: ThemeMode.DARK.name) }
            .getOrDefault(ThemeMode.DARK)

    private companion object {
        const val KEY_THEME = "theme"
        const val KEY_LAST_SYNC = "last_sync"
        const val KEY_SEED_VERSION = "seed_version"
    }
}
