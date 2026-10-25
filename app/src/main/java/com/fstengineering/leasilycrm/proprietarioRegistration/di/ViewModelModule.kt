package com.fstengineering.leasilycrm.proprietarioRegistration.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.ProprietarioRegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val proprietarioRegistrationViewModelModule = module {
    viewModel {
        ProprietarioRegistrationViewModel(
            route = get<SavedStateHandle>().toRoute(),
            writeProprietario = get(),
            proprietarioRegistrationReadDataSource = get(),
            sharedStorageDataSource = get(),
        )
    }
}
