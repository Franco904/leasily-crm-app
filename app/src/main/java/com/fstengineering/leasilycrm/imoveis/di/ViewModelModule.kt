package com.fstengineering.leasilycrm.imoveis.di

import com.fstengineering.leasilycrm.imoveis.presentation.ImoveisViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val imoveisViewModelModule = module {
    viewModel {
        ImoveisViewModel(
            imoveisReadDataSource = get(),
            imovelSharedWriteDataSource = get(),
        )
    }
}
