package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.assistanceRegistration.presentation.AssistanceRegistrationViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.readAssistanceQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writeAssistanceQualifier
import com.fstengineering.leasilycrm.home.HomeViewModel
import com.fstengineering.leasilycrm.home.dashboard.DashboardViewModel
import com.fstengineering.leasilycrm.home.leads.LeadsViewModel
import com.fstengineering.leasilycrm.home.stays.StaysViewModel
import com.fstengineering.leasilycrm.home.tenants.TenantsViewModel
import com.fstengineering.leasilycrm.leadTenantDetails.LeadTenantDetailsViewModel
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.LeadTenantRegistrationViewModel
import com.fstengineering.leasilycrm.profile.presentation.ProfileViewModel
import com.fstengineering.leasilycrm.properties.presentation.PropertiesViewModel
import com.fstengineering.leasilycrm.propertyDashboard.presentation.PropertyDashboardViewModel
import com.fstengineering.leasilycrm.propertyDetails.presentation.PropertyDetailsViewModel
import com.fstengineering.leasilycrm.propertyOwnerDetails.presentation.PropertyOwnerDetailsViewModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.PropertyOwnerRegistrationViewModel
import com.fstengineering.leasilycrm.propertyOwners.presentation.PropertyOwnersViewModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.PropertyRegistrationViewModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels.PropertyRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels.PropertyRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels.PropertyRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.propertyOccupation.presentation.PropertyOccupationViewModel
import com.fstengineering.leasilycrm.root.RootViewModel
import com.fstengineering.leasilycrm.settings.SettingsViewModel
import com.fstengineering.leasilycrm.signin.presentation.SignInViewModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.OccupiedPeriodDetailsViewModel
import com.fstengineering.leasilycrm.stayRegistration.presentation.StayRegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        RootViewModel(
            userSessionSharedStore = get(),
            settingsSharedStore = get(),
        )
    }

    viewModel {
        SignInViewModel(
            userSessionGateway = get(),
            connectivityService = get(),
        )
    }

    viewModel {
        SettingsViewModel(
            settingsSharedQueryRepository = get(),
            settingsSharedStore = get(),
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
            staySharedCommandRepository = get(),
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
            propertySharedCommandRepository = get(),
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
            watchProperty = get(),
            propertySharedCommandRepository = get(),
        )
    }

    viewModel {
        PropertyDetailsViewModel(
            propertyRepository = get(),
        )
    }

    viewModel {
        PropertyOccupationViewModel(
            watchPropertyOccupation = get(),
            dateProvider = get(),
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
            leadTenantSharedCommandRepository = get(),
        )
    }

    viewModel {
        TenantsViewModel(
            leadTenantSharedCommandRepository = get(),
        )
    }

    viewModel {
        LeadTenantRegistrationViewModel(
            tenantRepository = get(),
        )
    }

    viewModel {
        LeadTenantDetailsViewModel(
            leadTenantSharedCommandRepository = get(),
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
            staySharedCommandRepository = get(),
            now = AppDateTime.lazyNow(),
        )
    }


    viewModel {
        StayRegistrationViewModel(
            readStay = get(),
            readUnavailablePeriod = get(),
            readSelectableProperties = get(),
            writeStay = get(),
            writeUnavailablePeriod = get(),
            propertyQueryRepository = get(),
            tenantQueryRepository = get(),
            fileStorageService = get(),
        )
    }

    viewModel {
        OccupiedPeriodDetailsViewModel(
            watchOccupiedPeriod = get(),
            deleteStayUseCase = get(),
            registerStayCheckInUseCase = get(),
            undoRegisterStayCheckInUseCase = get(),
            registerStayCheckOut = get(),
            undoRegisterStayCheckOut = get(),
            watchMustNotShowCheckOutDialogAnymoreUseCase = get(),
            setMustNotShowCheckOutDialogAnymoreUseCase = get(),
        )
    }
}
