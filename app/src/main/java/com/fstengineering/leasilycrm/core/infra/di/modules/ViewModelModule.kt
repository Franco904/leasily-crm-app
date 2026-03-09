package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.assistanceRegistration.presentation.AssistanceRegistrationViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.readAssistanceQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writeAssistanceQualifier
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
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.PropertyOwnerRegistrationViewModel
import com.fstengineering.leasilycrm.propertyOwners.PropertyOwnersViewModel
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationViewModel
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.propertyStays.PropertyStaysViewModel
import com.fstengineering.leasilycrm.root.RootViewModel
import com.fstengineering.leasilycrm.settings.SettingsViewModel
import com.fstengineering.leasilycrm.signin.presentation.SignInViewModel
import com.fstengineering.leasilycrm.stayDetails.StayDetailsViewModel
import com.fstengineering.leasilycrm.stayRegistration.StayRegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        RootViewModel(
            userSessionRepository = get(),
            settingsSharedCommandRepository = get(),
        )
    }

    viewModel {
        SignInViewModel(
            signInRepository = get(),
            connectivityService = get(),
        )
    }

    viewModel {
        SettingsViewModel(
            settingsSharedQueryRepository = get(),
            settingsSharedCommandRepository = get(),
        )
    }

    viewModel {
        ProfileViewModel(
            writeUser = get(),
            connectivityService = get(),
            userSessionService = get(),
            userQueryRepository = get(),
        )
    }

    viewModel {
        HomeViewModel(
            assistanceCommandRepository = get(),
            stayRepository = get(),
            userQueryRepository = get(),
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
            propertyOwnerSharedCommandRepository = get(),
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
            propertyOwnerSharedCommandRepository = get(),
        )
    }

    viewModel {
        PropertyOwnerRegistrationViewModel(
            writePropertyOwner = get(),
            propertyOwnerQueryRepository = get(),
            fileStorageService = get(),
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
            assistanceCommandRepository = get(),
        )
    }

    viewModel {
        PropertyOwnerDetailsViewModel(
            propertyOwnerSharedCommandRepository = get(),
        )
    }

    viewModel {
        AssistanceRegistrationViewModel(
            assistanceQueryRepository = get(),
            readAssistance = get(readAssistanceQualifier),
            writeAssistance = get(writeAssistanceQualifier),
        )
    }

    viewModel {
        AssistanceRegistrationStep1ViewModel()
    }

    viewModel {
        AssistanceRegistrationStep2ViewModel(
            assistanceSelectablePropertyQueryRepository = get(),
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
