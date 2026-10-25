package com.fstengineering.leasilycrm.proprietarios.di

import com.fstengineering.leasilycrm.proprietarios.presentation.ProprietariosViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val proprietariosViewModelModule = module {
    viewModel {
        ProprietariosViewModel(
            proprietarioQueryRepository = get(),
            proprietarioSharedCommandRepository = get(),
        )
    }
}
