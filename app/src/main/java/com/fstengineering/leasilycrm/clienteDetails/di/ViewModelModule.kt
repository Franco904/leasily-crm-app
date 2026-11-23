package com.fstengineering.leasilycrm.clienteDetails.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.clienteDetails.presentation.ClienteDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val clienteDetailsViewModelModule = module {
    viewModel {
        ClienteDetailsViewModel(
            route = get<SavedStateHandle>().toRoute(),
            clienteDetailsReadDataSource = get(),
            clienteWriteDataSource = get(),
            atendimentoSharedWriteDataSource = get(),
        )
    }
}
