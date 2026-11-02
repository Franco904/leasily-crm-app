package com.fstengineering.leasilycrm.imovelDetails.di

import com.fstengineering.leasilycrm.imovelDetails.presentation.ImovelDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val imovelDetailsViewModelModule = module {
    viewModel {
        ImovelDetailsViewModel(
            imovelQueryRepository = get(),
            imovelSharedCommandRepository = get(),
        )
    }
}
