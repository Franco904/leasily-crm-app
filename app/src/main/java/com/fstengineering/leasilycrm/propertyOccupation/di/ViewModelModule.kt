package com.fstengineering.leasilycrm.propertyOccupation.di

import com.fstengineering.leasilycrm.propertyOccupation.presentation.PropertyOccupationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val propertyOccupationViewModelModule = module {
    viewModel {
        PropertyOccupationViewModel(
            watchPropertyOccupation = get(),
            dateProvider = get(),
        )
    }
}
