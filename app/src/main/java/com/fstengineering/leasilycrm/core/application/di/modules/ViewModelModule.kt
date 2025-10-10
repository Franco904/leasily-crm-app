package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.core.application.models.AppLazyDate
import com.fstengineering.leasilycrm.core.application.models.AppLazyDateTime
import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsViewModel
import com.fstengineering.leasilycrm.home.HomeViewModel
import com.fstengineering.leasilycrm.home.leads.LeadsViewModel
import com.fstengineering.leasilycrm.home.stays.StaysViewModel
import com.fstengineering.leasilycrm.home.tenants.TenantsViewModel
import com.fstengineering.leasilycrm.leadTenantDetails.LeadTenantDetailsViewModel
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationViewModel
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
            settingsRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        SignInViewModel(
            userRepository = get(),
        )
    }

    viewModel {
        SettingsViewModel(
            settingsRepository = get(),
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
            deleteCurrentAssistanceUseCase = get(),
            stayRepository = get(),
            userRepository = get(),
            now = AppLazyDateTime.now(),
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
            getStaysStructuredDatesUseCase = get(),
            watchPropertyWithStaysTenantsUseCase = get(),
            propertyRepository = get(),
            userRepository = get(),
            now = AppLazyDate.now(),
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
        PropertyStaysViewModel(
            getStaysStructuredDatesUseCase = get(),
            watchPropertyWithStaysTenantsUseCase = get(),
            propertyRepository = get(),
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
        LeadsViewModel(
            tenantRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        TenantsViewModel(
            tenantRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        LeadTenantRegistrationViewModel(
            tenantRepository = get(),
            customerSegmentRepository = get(),
            tenantContactOriginTypeRepository = get(),
            userRepository = get(),
            tenantValidator = get(),
        )
    }

    viewModel {
        LeadTenantDetailsViewModel(
            deleteCurrentAssistanceUseCase = get(),
            tenantRepository = get(),
            assistanceCharacteristicRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        PropertyOwnerDetailsViewModel(
            propertyOwnerRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        AssistanceRegistrationViewModel(
            assistanceRepository = get(),
            tenantRepository = get(),
            assistanceSearchedNeighborhoodRepository = get(),
            propertySeaProximityTypeRepository = get(),
            assistanceCharacteristicRepository = get(),
            assistanceCharacteristicAssistanceRepository = get(),
            assistanceSentPropertyRepository = get(),
            assistanceInteractionRepository = get(),
            userRepository = get(),
        )
    }

    viewModel {
        AssistanceRegistrationStep1ViewModel(
            assistanceValidator = get(),
            assistanceSearchedNeighborhoodValidator = get(),
        )
    }

    viewModel {
        AssistanceRegistrationStep2ViewModel(
            propertyRepository = get(),
        )
    }

    viewModel {
        AssistanceRegistrationStep3ViewModel(
            assistanceInteractionValidator = get(),
        )
    }

    viewModel {
        StaysViewModel(
            deleteStayUseCase = get(),
            registerStayCheckOutUseCase = get(),
            undoRegisterStayCheckOutUseCase = get(),
            stayRepository = get(),
            userRepository = get(),
            now = AppLazyDateTime.now(),
        )
    }

    viewModel {
        StayRegistrationViewModel(
            stayRepository = get(),
            propertyRepository = get(),
            tenantRepository = get(),
            stayTransferRepository = get(),
            stayFileRepository = get(),
            userRepository = get(),
            stayValidator = get(),
            stayTransferValidator = get(),
        )
    }

    viewModel {
        StayDetailsViewModel(
            deleteStayUseCase = get(),
            registerStayCheckOutUseCase = get(),
            undoRegisterStayCheckOutUseCase = get(),
            getStaysStructuredDatesUseCase = get(),
            stayRepository = get(),
            userRepository = get(),
        )
    }
}
