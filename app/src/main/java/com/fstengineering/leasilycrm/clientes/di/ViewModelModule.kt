package com.fstengineering.leasilycrm.clientes.di

import com.fstengineering.leasilycrm.clientes.presentation.hospedes.HospedesViewModel
import com.fstengineering.leasilycrm.clientes.presentation.leads.LeadsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val clientesViewModelModule = module {
    viewModel {
        LeadsViewModel(
            clientesReadDataSource = get(),
            clienteWriteDataSource = get(),
            clientesPreferencesDataSource = get(),
        )
    }

    viewModel {
        HospedesViewModel(
            clientesReadDataSource = get(),
            clienteWriteDataSource = get(),
            clientesPreferencesDataSource = get(),
        )
    }
}
