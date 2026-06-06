package com.fstengineering.leasilycrm.propertyOccupation.di

import com.fstengineering.leasilycrm.core.di.qualifiers.watchPropertyOccupationQualifier
import com.fstengineering.leasilycrm.propertyOccupation.presentation.PropertyOccupationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val propertyOccupationViewModelModule = module {
    viewModel {
        PropertyOccupationViewModel(
            watchPropertyOccupation = get(watchPropertyOccupationQualifier),
            dateProvider = get(),
        )
    }
}
