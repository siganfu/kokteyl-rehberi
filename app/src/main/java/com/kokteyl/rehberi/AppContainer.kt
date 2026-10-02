package com.kokteyl.rehberi

import android.app.Application
import com.kokteyl.rehberi.data.local.AppDatabase
import com.kokteyl.rehberi.data.remote.NetworkModule
import com.kokteyl.rehberi.data.repository.CocktailRepository
import com.kokteyl.rehberi.data.settings.SettingsStore
import com.kokteyl.rehberi.translate.TextTranslator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Basit elle bağımlılık enjeksiyonu (Hilt kullanmadan, kodu sade tutmak için). */
class AppContainer(private val app: Application) {
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val settings = SettingsStore(app)
    private val database by lazy { AppDatabase.build(app) }
    private val translator = TextTranslator()
    val repository: CocktailRepository by lazy {
        CocktailRepository(app, database, NetworkModule.api, translator, settings)
    }

    fun startup() {
        appScope.launch { repository.initialize() }
    }
}
