package com.fstengineering.leasilycrm.propertyDetails.di

import com.fstengineering.leasilycrm.propertyDetails.presentation.PropertyDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val propertyDetailsViewModelModule = module {
    viewModel {
        PropertyDetailsViewModel(
            propertyQueryRepository = get(),
            propertySharedCommandRepository = get(),
        )
    }
}
