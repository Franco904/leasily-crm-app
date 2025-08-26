package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsViewModel
import com.fstengineering.leasilycrm.home.HomeViewModel
import com.fstengineering.leasilycrm.profile.ProfileViewModel
import com.fstengineering.leasilycrm.propertyOwners.PropertyOwnersViewModel
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
        PropertyOwnersViewModel(
            propertyOwnerRepository = get(),
            userRepository = get(),
            propertyOwnerValidator = get(),
        )
    }
}
