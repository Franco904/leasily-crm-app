package com.fstengineering.leasilycrm.propertyOwnerRegistration.di

import com.fstengineering.leasilycrm.core.di.qualifiers.writePropertyOwnerQualifier
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.PropertyOwnerRegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val propertyOwnerRegistrationViewModelModule = module {
    viewModel {
        PropertyOwnerRegistrationViewModel(
            writePropertyOwner = get(writePropertyOwnerQualifier),
            propertyOwnerQueryRepository = get(),
            fileStorageService = get(),
        )
    }
}
