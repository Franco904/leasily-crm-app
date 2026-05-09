package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.assistanceRegistration.presentation.AssistanceRegistrationViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedQueryRepository
import com.fstengineering.leasilycrm.dashboard.presentation.DashboardViewModel
import com.fstengineering.leasilycrm.home.HomeViewModel
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.LeadTenantDetailsViewModel
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.LeadTenantRegistrationViewModel
import com.fstengineering.leasilycrm.leadTenants.presentation.leads.LeadsViewModel
import com.fstengineering.leasilycrm.leadTenants.presentation.tenants.TenantsViewModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.OccupiedPeriodDetailsViewModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.OccupiedPeriodRegistrationViewModel
import com.fstengineering.leasilycrm.occupiedPeriods.presentation.OccupiedPeriodsViewModel
import com.fstengineering.leasilycrm.profile.presentation.ProfileViewModel
import com.fstengineering.leasilycrm.properties.presentation.PropertiesViewModel
import com.fstengineering.leasilycrm.propertyDashboard.presentation.PropertyDashboardViewModel
import com.fstengineering.leasilycrm.propertyDetails.presentation.PropertyDetailsViewModel
import com.fstengineering.leasilycrm.propertyOccupation.presentation.PropertyOccupationViewModel
import com.fstengineering.leasilycrm.propertyOwnerDetails.presentation.PropertyOwnerDetailsViewModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.PropertyOwnerRegistrationViewModel
import com.fstengineering.leasilycrm.propertyOwners.presentation.PropertyOwnersViewModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.PropertyRegistrationViewModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels.PropertyRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels.PropertyRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels.PropertyRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.root.RootViewModel
import com.fstengineering.leasilycrm.settings.SettingsViewModel
import com.fstengineering.leasilycrm.signin.presentation.SignInViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import com.fstengineering.leasilycrm.leadTenants.application.repositories.LeadTenantCommandRepository as LeadTenantsLeadTenantCommandRepository
import com.fstengineering.leasilycrm.leadTenants.application.repositories.LeadTenantQueryRepository as LeadTenantsLeadTenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriods.application.repositories.OccupiedPeriodQueryRepository as OccupiedPeriodsQueryRepository

val viewModelModule = module {
    viewModel {
        RootViewModel(
            userSessionSharedStore = get(),
            settingsSharedStore = get(),
        )
    }

    viewModel {
        SignInViewModel(
            signIn = get(),
            connectivityService = get(),
        )
    }

    viewModel {
        SettingsViewModel(
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
            occupiedPeriodSharedQueryRepository = get(),
            userQueryRepository = get(),
        )
    }

    viewModel {
        DashboardViewModel(
            crmMetricsRepository = get(),
        )
    }

    viewModel {
        PropertiesViewModel(
            propertyQueryRepository = get(),
            propertySharedCommandRepository = get(),
        )
    }

    viewModel {
        PropertyRegistrationViewModel(
            readProperty = get(),
            writeProperty = get(),
            propertyQueryRepository = get(),
            propertyOwnerQueryRepository = get(),
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
            propertyQueryRepository = get(),
            propertySharedCommandRepository = get(),
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
            propertyOwnerQueryRepository = get(),
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
            leadTenantQueryRepository = get<LeadTenantsLeadTenantQueryRepository>(),
            leadTenantCommandRepository = get<LeadTenantsLeadTenantCommandRepository>(),
        )
    }

    viewModel {
        TenantsViewModel(
            leadTenantQueryRepository = get<LeadTenantsLeadTenantQueryRepository>(),
            leadTenantCommandRepository = get<LeadTenantsLeadTenantCommandRepository>(),
        )
    }

    viewModel {
        LeadTenantRegistrationViewModel(
            writeLeadTenant = get(),
            leadTenantQueryRepository = get(),
        )
    }

    viewModel {
        LeadTenantDetailsViewModel(
            watchLeadTenant = get(),
            deleteLeadTenant = get(),
            deleteAssistance = get(),
        )
    }

    viewModel {
        PropertyOwnerDetailsViewModel(
            propertyOwnerQueryRepository = get(),
            propertyOwnerSharedCommandRepository = get(),
        )
    }

    viewModel {
        AssistanceRegistrationViewModel(
            assistanceQueryRepository = get(),
            readAssistance = get(),
            writeAssistance = get(),
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
        OccupiedPeriodsViewModel(
            registerStayCheckOutUseCase = get(),
            undoRegisterStayCheckOutUseCase = get(),
            occupiedPeriodSharedCommandRepository = get(),
            occupiedPeriodQueryRepository = get<OccupiedPeriodsQueryRepository>(),
            occupiedPeriodSharedQueryRepository = get<OccupiedPeriodSharedQueryRepository>(),
        )
    }

    viewModel {
        OccupiedPeriodRegistrationViewModel(
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
            registerStayCheckOut = get(),
            undoRegisterStayCheckOut = get(),
            occupiedPeriodSharedCommandRepository = get(),
            occupiedPeriodQueryRepository = get(),
        )
    }
}
