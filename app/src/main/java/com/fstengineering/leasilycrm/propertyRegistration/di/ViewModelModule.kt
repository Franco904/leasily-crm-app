package com.fstengineering.leasilycrm.propertyRegistration.di

import com.fstengineering.leasilycrm.core.di.qualifiers.readPropertyQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.writePropertyQualifier
import com.fstengineering.leasilycrm.propertyRegistration.presentation.PropertyRegistrationViewModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels.PropertyRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels.PropertyRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels.PropertyRegistrationStep3ViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val propertyRegistrationViewModelModule = module {
    viewModel {
        PropertyRegistrationViewModel(
            readProperty = get(readPropertyQualifier),
            writeProperty = get(writePropertyQualifier),
            propertyQueryRepository = get(),
            propertyOwnerQueryRepository = get(),
        )
    }

    viewModel { PropertyRegistrationStep1ViewModel() }

    viewModel { PropertyRegistrationStep2ViewModel() }

    viewModel { PropertyRegistrationStep3ViewModel() }
}
