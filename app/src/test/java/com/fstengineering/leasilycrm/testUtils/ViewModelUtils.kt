package com.fstengineering.leasilycrm.testUtils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore

/** Descarta o ViewModel pelo caminho do Android: limpar o [ViewModelStore] dispara `onCleared`. */
fun ViewModel.clearFromStore() {
    val viewModel = this
    val store = ViewModelStore()
    val factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            requireNotNull(modelClass.cast(viewModel))
    }
    ViewModelProvider(store, factory)[viewModel.javaClass]

    store.clear()
}
