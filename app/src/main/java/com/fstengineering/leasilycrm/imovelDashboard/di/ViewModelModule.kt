package com.fstengineering.leasilycrm.imovelDashboard.di

import com.fstengineering.leasilycrm.imovelDashboard.presentation.ImovelDashboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val imovelDashboardViewModelModule = module {
    viewModel {
        ImovelDashboardViewModel(
            watchImovel = get(),
            imovelSharedCommandRepository = get(),
        )
    }
}
