package com.fstengineering.leasilycrm.imovelDetails.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.imovelDetails.presentation.ImovelDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val imovelDetailsViewModelModule = module {
    viewModel {
        ImovelDetailsViewModel(
            route = get<SavedStateHandle>().toRoute(),
            imovelDetailsReadDataSource = get(),
            imovelSharedWriteDataSource = get(),
        )
    }
}
