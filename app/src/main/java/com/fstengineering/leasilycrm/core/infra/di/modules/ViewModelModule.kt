package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.assistanceRegistration.presentation.AssistanceRegistrationViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.home.HomeViewModel
import com.fstengineering.leasilycrm.home.dashboard.DashboardViewModel
import com.fstengineering.leasilycrm.home.leads.LeadsViewModel
import com.fstengineering.leasilycrm.home.stays.StaysViewModel
import com.fstengineering.leasilycrm.home.tenants.TenantsViewModel
import com.fstengineering.leasilycrm.leadTenantDetails.LeadTenantDetailsViewModel
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationViewModel
import com.fstengineering.leasilycrm.profile.presentation.ProfileViewModel
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
import com.fstengineering.leasilycrm.propertyStays.PropertyStaysViewModel
import com.fstengineering.leasilycrm.root.RootViewModel
import com.fstengineering.leasilycrm.settings.SettingsViewModel
import com.fstengineering.leasilycrm.signin.SignInViewModel
import com.fstengineering.leasilycrm.stayDetails.StayDetailsViewModel
import com.fstengineering.leasilycrm.stayRegistration.StayRegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        RootViewModel(
            authRepository = get(),
            settingsRepository = get(),
        )
    }

    viewModel {
        SignInViewModel(
            authRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        SettingsViewModel(
            settingsRepository = get(),
        )
    }

    viewModel {
        ProfileViewModel(
            authRepository = get(),
            userRepository = get(),
            writeUser = get(),
            userSessionService = get(),
        )
    }

    viewModel {
        HomeViewModel(
            assistanceRepository = get(),
            stayRepository = get(),
            userRepository = get(),
            now = AppDateTime.lazyNow(),
        )
    }

    viewModel {
        DashboardViewModel(
            crmMetricsRepository = get(),
        )
    }

    viewModel {
        PropertiesViewModel(
            propertyRepository = get(),
        )
    }

    viewModel {
        PropertyRegistrationViewModel(
            propertyRepository = get(),
            propertyOwnerRepository = get(),
        )
    }

    viewModel {
        PropertyRegistrationStep1ViewModel()
    }

    viewModel {
        PropertyRegistrationStep2ViewModel()
    }

    viewModel {
        PropertyRegistrationStep3ViewModel()
    }

    viewModel {
        PropertyDashboardViewModel(
            getStaysStructuredDatesUseCase = get(),
            watchPropertyWithStaysTenantsUseCase = get(),
            propertyRepository = get(),
            now = AppDate.lazyNow(),
        )
    }

    viewModel {
        PropertyDetailsViewModel(
            propertyRepository = get(),
        )
    }

    viewModel {
        PropertyStaysViewModel(
            getStaysStructuredDatesUseCase = get(),
            watchPropertyWithStaysTenantsUseCase = get(),
            propertyRepository = get(),
            now = AppDate.lazyNow(),
        )
    }

    viewModel {
        PropertyOwnersViewModel(
            propertyOwnerRepository = get(),
        )
    }

    viewModel {
        PropertyOwnerRegistrationViewModel(
            propertyOwnerRepository = get(),
            propertyOwnerFileRepository = get(),
        )
    }

    viewModel {
        LeadsViewModel(
            tenantRepository = get(),
        )
    }

    viewModel {
        TenantsViewModel(
            tenantRepository = get(),
        )
    }

    viewModel {
        LeadTenantRegistrationViewModel(
            tenantRepository = get(),
        )
    }

    viewModel {
        LeadTenantDetailsViewModel(
            tenantRepository = get(),
            assistanceRepository = get(),
        )
    }

    viewModel {
        PropertyOwnerDetailsViewModel(
            propertyOwnerRepository = get(),
        )
    }

    viewModel {
        AssistanceRegistrationViewModel(
            assistanceRepository = get(),
            tenantRepository = get(),
        )
    }

    viewModel {
        AssistanceRegistrationStep1ViewModel()
    }

    viewModel {
        AssistanceRegistrationStep2ViewModel(
            propertyRepository = get(),
        )
    }

    viewModel {
        AssistanceRegistrationStep3ViewModel()
    }

    viewModel {
        StaysViewModel(
            registerStayCheckOutUseCase = get(),
            undoRegisterStayCheckOutUseCase = get(),
            stayRepository = get(),
            now = AppDateTime.lazyNow(),
        )
    }

    viewModel {
        StayRegistrationViewModel(
            stayRepository = get(),
            propertyRepository = get(),
            tenantRepository = get(),
            stayFileRepository = get(),
        )
    }

    viewModel {
        StayDetailsViewModel(
            registerStayCheckOutUseCase = get(),
            undoRegisterStayCheckOutUseCase = get(),
            getStaysStructuredDatesUseCase = get(),
            stayRepository = get(),
        )
    }
}
