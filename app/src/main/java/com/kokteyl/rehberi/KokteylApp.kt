package com.kokteyl.rehberi

import android.app.Application

class KokteylApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.startup()
    }
}
