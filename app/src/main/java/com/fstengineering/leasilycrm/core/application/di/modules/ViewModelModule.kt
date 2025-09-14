package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsViewModel
import com.fstengineering.leasilycrm.home.HomeViewModel
import com.fstengineering.leasilycrm.profile.ProfileViewModel
import com.fstengineering.leasilycrm.properties.PropertiesViewModel
import com.fstengineering.leasilycrm.propertyDashboard.PropertyDashboardViewModel
import com.fstengineering.leasilycrm.propertyDetails.PropertyDetailsViewModel
import com.fstengineering.leasilycrm.propertyOwnerDetails.PropertyOwnerDetailsViewModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.PropertyOwnerRegistrationViewModel
import com.fstengineering.leasilycrm.propertyOwners.PropertyOwnersViewModel
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationViewModel
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.root.RootViewModel
import com.fstengineering.leasilycrm.signin.SignInViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        RootViewModel(
            userRepository = get(),
        )
    }

    viewModel {
        SignInViewModel(
            userRepository = get(),
        )
    }

    viewModel {
        ProfileViewModel(
            userValidator = get(),
            userRepository = get(),
        )
    }

    viewModel {
        HomeViewModel(
            userRepository = get(),
        )
    }

    viewModel {
        CustomerSegmentsViewModel(
            customerSegmentRepository = get(),
            customerSegmentTypeRepository = get(),
            userRepository = get(),
            customerSegmentValidator = get(),
        )
    }

    viewModel {
        PropertiesViewModel(
            propertyRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        PropertyRegistrationViewModel(
            propertyRepository = get(),
            propertyOwnerRepository = get(),
            propertyTypeRepository = get(),
            propertySeaProximityTypeRepository = get(),
            propertyCharacteristicRepository = get(),
            propertyCharacteristicPropertyRepository = get(),
            propertyPriceRepository = get(),
            propertyFileRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        PropertyRegistrationStep1ViewModel(
            propertyValidator = get(),
        )
    }

    viewModel {
        PropertyRegistrationStep2ViewModel(
            propertyValidator = get(),
        )
    }

    viewModel {
        PropertyRegistrationStep3ViewModel(
            propertyPriceValidator = get(),
        )
    }

    viewModel {
        PropertyDashboardViewModel(
            propertyRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        PropertyDetailsViewModel(
            propertyRepository = get(),
            propertyCharacteristicRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        PropertyOwnersViewModel(
            propertyOwnerRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        PropertyOwnerRegistrationViewModel(
            propertyOwnerRepository = get(),
            propertyOwnerFileRepository = get(),
            customerSegmentRepository = get(),
            userRepository = get(),
            propertyOwnerValidator = get(),
        )
    }

    viewModel {
        PropertyOwnerDetailsViewModel(
            propertyOwnerRepository = get(),
            userRepository = get(),
        )
    }
}
