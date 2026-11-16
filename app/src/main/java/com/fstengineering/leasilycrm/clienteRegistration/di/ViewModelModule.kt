package com.fstengineering.leasilycrm.clienteRegistration.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.clienteRegistration.presentation.ClienteRegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val clienteRegistrationViewModelModule = module {
    viewModel {
        ClienteRegistrationViewModel(
            route = get<SavedStateHandle>().toRoute(),
            clienteWriteDataSource = get(),
            clienteRegistrationReadDataSource = get(),
        )
    }
}
