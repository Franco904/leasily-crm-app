package com.fstengineering.leasilycrm.proprietarioRegistration.di

import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.ProprietarioRegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val proprietarioRegistrationViewModelModule = module {
    viewModel {
        ProprietarioRegistrationViewModel(
            writeProprietario = get(),
            proprietarioQueryRepository = get(),
            sharedStorageDataSource = get(),
        )
    }
}
