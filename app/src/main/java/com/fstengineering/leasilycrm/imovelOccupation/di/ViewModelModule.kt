package com.fstengineering.leasilycrm.imovelOccupation.di

import com.fstengineering.leasilycrm.imovelOccupation.presentation.ImovelOccupationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val imovelOccupationViewModelModule = module {
    viewModel {
        ImovelOccupationViewModel(
            watchImovelOccupation = get(),
            dateProvider = get(),
        )
    }
}
