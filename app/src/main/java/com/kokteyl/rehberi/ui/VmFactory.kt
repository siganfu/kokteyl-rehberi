package com.kokteyl.rehberi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/** Basit ViewModel fabrikası: viewModel(factory = vmFactory { MyViewModel(repo) }) */
inline fun <reified VM : ViewModel> vmFactory(crossinline builder: () -> VM): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = builder() as T
    }
