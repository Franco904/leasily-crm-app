package com.fstengineering.leasilycrm.proprietarioDetails.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.ProprietarioDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val proprietarioDetailsViewModelModule = module {
    viewModel {
        ProprietarioDetailsViewModel(
            route = get<SavedStateHandle>().toRoute(),
            proprietarioDetailsReadDataSource = get(),
            proprietarioWriteDataSource = get(),
        )
    }
}
