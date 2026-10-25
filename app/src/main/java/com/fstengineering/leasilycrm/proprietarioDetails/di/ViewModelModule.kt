package com.fstengineering.leasilycrm.proprietarioDetails.di

import com.fstengineering.leasilycrm.proprietarioDetails.presentation.ProprietarioDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val proprietarioDetailsViewModelModule = module {
    viewModel {
        ProprietarioDetailsViewModel(
            proprietarioQueryRepository = get(),
            proprietarioSharedCommandRepository = get(),
        )
    }
}
