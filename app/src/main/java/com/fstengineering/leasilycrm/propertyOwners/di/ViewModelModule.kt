package com.fstengineering.leasilycrm.propertyOwners.di

import com.fstengineering.leasilycrm.propertyOwners.presentation.PropertyOwnersViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val propertyOwnersViewModelModule = module {
    viewModel {
        PropertyOwnersViewModel(
            propertyOwnerQueryRepository = get(),
            propertyOwnerSharedCommandRepository = get(),
        )
    }
}
