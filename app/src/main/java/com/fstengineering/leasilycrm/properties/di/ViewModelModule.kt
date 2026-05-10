package com.fstengineering.leasilycrm.properties.di

import com.fstengineering.leasilycrm.properties.presentation.PropertiesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val propertiesViewModelModule = module {
    viewModel {
        PropertiesViewModel(
            propertyQueryRepository = get(),
            propertySharedCommandRepository = get(),
        )
    }
}
