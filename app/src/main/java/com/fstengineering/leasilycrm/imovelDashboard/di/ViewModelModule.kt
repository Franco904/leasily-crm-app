package com.fstengineering.leasilycrm.imovelDashboard.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.imovelDashboard.presentation.ImovelDashboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val imovelDashboardViewModelModule = module {
    viewModel {
        ImovelDashboardViewModel(
            route = get<SavedStateHandle>().toRoute(),
            watchImovel = get(),
            imovelSharedWriteDataSource = get(),
        )
    }
}
