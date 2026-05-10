package com.fstengineering.leasilycrm.propertyOwnerDetails.di

import com.fstengineering.leasilycrm.propertyOwnerDetails.presentation.PropertyOwnerDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val propertyOwnerDetailsViewModelModule = module {
    viewModel {
        PropertyOwnerDetailsViewModel(
            propertyOwnerQueryRepository = get(),
            propertyOwnerSharedCommandRepository = get(),
        )
    }
}
