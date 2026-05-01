package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSentPropertyQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceCommandRepositoryImpl
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceQueryRepositoryImpl
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceSentPropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.core.application.gateways.UserSessionSharedGateway
import com.fstengineering.leasilycrm.home.subdomains.dashboard.application.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedQueryRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertyOwnerSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.stores.SettingsSharedStore
import com.fstengineering.leasilycrm.core.application.stores.UserSessionSharedStore
import com.fstengineering.leasilycrm.core.domain.providers.AppDateProviderImpl
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.gateways.UserSessionSharedGatewayImpl
import com.fstengineering.leasilycrm.home.subdomains.dashboard.infra.repositories.CrmMetricsRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.LeadTenantSharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.LeadTenantSharedQueryRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.PropertyOwnerSharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.PropertySharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.OccupiedPeriodSharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.proxies.TenantRepositoryProxy
import com.fstengineering.leasilycrm.core.infra.stores.SettingsSharedStoreImpl
import com.fstengineering.leasilycrm.core.infra.stores.UserSessionSharedStoreImpl
import com.fstengineering.leasilycrm.leadTenantRegistration.application.repositories.LeadTenantCommandRepository
import com.fstengineering.leasilycrm.leadTenantRegistration.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.leadTenantRegistration.infra.repositories.LeadTenantCommandRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantRegistration.infra.repositories.LeadTenantQueryRepositoryImpl
import com.fstengineering.leasilycrm.profile.application.repositories.UserCommandRepository
import com.fstengineering.leasilycrm.profile.application.repositories.UserQueryRepository
import com.fstengineering.leasilycrm.profile.infra.repositories.UserCommandRepositoryImpl
import com.fstengineering.leasilycrm.profile.infra.repositories.UserQueryRepositoryImpl
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories.PropertyOwnerCommandRepository
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyOwnerRegistration.infra.repositories.PropertyOwnerCommandRepositoryImpl
import com.fstengineering.leasilycrm.propertyOwnerRegistration.infra.repositories.PropertyOwnerQueryRepositoryImpl
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.PropertyOwnerQueryRepository as PropertyDashboardPropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.StayQueryRepository as PropertyDashboardStayQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.infra.repositories.PropertyOwnerQueryRepositoryImpl as PropertyDashboardPropertyOwnerQueryRepositoryImpl
import com.fstengineering.leasilycrm.propertyDashboard.infra.repositories.StayQueryRepositoryImpl as PropertyDashboardStayQueryRepositoryImpl
import com.fstengineering.leasilycrm.propertyDetails.application.repositories.PropertyQueryRepository as PropertyDetailsPropertyQueryRepository
import com.fstengineering.leasilycrm.propertyDetails.infra.repositories.PropertyQueryRepositoryImpl as PropertyDetailsPropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.propertyOccupation.application.repositories.OccupiedPeriodQueryRepository as PropertyOccupationStayQueryRepository
import com.fstengineering.leasilycrm.propertyOccupation.infra.repositories.OccupiedPeriodQueryRepositoryImpl as PropertyOccupationStayQueryRepositoryImpl
import com.fstengineering.leasilycrm.signin.application.gateways.UserSessionGateway
import com.fstengineering.leasilycrm.signin.infra.gateways.UserSessionGatewayImpl
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.AssistanceCommandRepository as StayDetailsAssistanceCommandRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.PropertyQueryRepository as StayDetailsPropertyQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.OccupiedPeriodQueryRepository as StayDetailsStayQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.TenantQueryRepository as StayDetailsTenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories.AssistanceCommandRepositoryImpl as StayDetailsAssistanceCommandRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories.PropertyQueryRepositoryImpl as StayDetailsPropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories.OccupiedPeriodQueryRepositoryImpl as StayDetailsStayQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories.TenantQueryRepositoryImpl as StayDetailsTenantQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.PropertyQueryRepository as StayRegistrationPropertyQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodCommandRepository as StayRegistrationStayCommandRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodQueryRepository as StayRegistrationStayQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.TenantQueryRepository as StayRegistrationTenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories.PropertyQueryRepositoryImpl as StayRegistrationPropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories.OccupiedPeriodCommandRepositoryImpl as StayRegistrationStayCommandRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories.OccupiedPeriodQueryRepositoryImpl as StayRegistrationStayQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories.TenantQueryRepositoryImpl as StayRegistrationTenantQueryRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.LeadTenantQueryRepository as LeadTenantDetailsLeadTenantQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.LeadTenantCommandRepository as LeadTenantDetailsLeadTenantCommandRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceQueryRepository as LeadTenantDetailsAssistanceQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceCommandRepository as LeadTenantDetailsAssistanceCommandRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceSentPropertyQueryRepository as LeadTenantDetailsAssistanceSentPropertyQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.StayQueryRepository as LeadTenantDetailsStayQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories.LeadTenantQueryRepositoryImpl as LeadTenantDetailsLeadTenantQueryRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories.LeadTenantCommandRepositoryImpl as LeadTenantDetailsLeadTenantCommandRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories.AssistanceQueryRepositoryImpl as LeadTenantDetailsAssistanceQueryRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories.AssistanceCommandRepositoryImpl as LeadTenantDetailsAssistanceCommandRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories.AssistanceSentPropertyQueryRepositoryImpl as LeadTenantDetailsAssistanceSentPropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories.StayQueryRepositoryImpl as LeadTenantDetailsStayQueryRepositoryImpl
import com.fstengineering.leasilycrm.home.application.repositories.LeadTenantQueryRepository as HomeLeadTenantQueryRepository
import com.fstengineering.leasilycrm.home.application.repositories.LeadTenantCommandRepository as HomeLeadTenantCommandRepository
import com.fstengineering.leasilycrm.home.infra.repositories.LeadTenantQueryRepositoryImpl as HomeLeadTenantQueryRepositoryImpl
import com.fstengineering.leasilycrm.home.infra.repositories.LeadTenantCommandRepositoryImpl as HomeLeadTenantCommandRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    // Assistance registration

    single<AssistanceCommandRepository> {
        AssistanceCommandRepositoryImpl(
            databaseAdapter = get(),
            assistanceDatabaseDataSource = get(),
            assistanceCharacteristicCrossRefDatabaseDataSource = get(),
            assistanceInteractionDatabaseDataSource = get(),
            assistanceSearchedNeighborhoodDatabaseDataSource = get(),
            assistanceSentPropertyCrossRefDatabaseDataSource = get(),
        )
    }

    single<AssistanceQueryRepository> {
        AssistanceQueryRepositoryImpl(
            assistanceDatabaseDataSource = get(),
        )
    }

    single<AssistanceSentPropertyQueryRepository> {
        AssistanceSentPropertyQueryRepositoryImpl(
            propertyDatabaseDataSource = get(),
        )
    }

    // TODO: improve this
    single<com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.LeadTenantQueryRepository> {
        com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.LeadTenantQueryRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    // Profile

    single<UserCommandRepository> {
        com.fstengineering.leasilycrm.profile.infra.repositories.UserCommandRepositoryImpl(
            remoteDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }

    single<UserQueryRepository> {
        UserQueryRepositoryImpl(
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }

    // Lead/tenant registration

    single<LeadTenantCommandRepository> {
        LeadTenantCommandRepositoryImpl(
            databaseAdapter = get(),
            databaseDataSource = get(),
        )
    }

    single<LeadTenantQueryRepository> {
        LeadTenantQueryRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    // Property owner registration

    single<PropertyOwnerCommandRepository> {
        PropertyOwnerCommandRepositoryImpl(
            databaseAdapter = get(),
            propertyOwnerDatabaseDataSource = get(),
            propertyOwnerFileDatabaseDataSource = get(),
        )
    }

    single<PropertyOwnerQueryRepository> {
        PropertyOwnerQueryRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    // Sign in

    single<UserSessionGateway> {
        UserSessionGatewayImpl(
            remoteDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }

    // Core

    single<UserSessionSharedGateway> {
        UserSessionSharedGatewayImpl(
            connectivityAdapter = get(),
            remoteDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }

    single<CrmMetricsRepository> {
        _root_ide_package_.com.fstengineering.leasilycrm.home.subdomains.dashboard.infra.repositories.CrmMetricsRepositoryImpl(
            databaseDataSource = get(),
            now = AppDate.lazyNow(),
        )
    }

    single<PropertyOwnerSharedCommandRepository> {
        PropertyOwnerSharedCommandRepositoryImpl(
            databaseAdapter = get(),
            propertyOwnerDatabaseDataSource = get(),
        )
    }

    single<PropertySharedCommandRepository> {
        PropertySharedCommandRepositoryImpl(
            databaseAdapter = get(),
            propertyDatabaseDataSource = get(),
            propertyCharacteristicCrossRefDatabaseDataSource = get(),
            propertyFileDatabaseDataSource = get(),
            propertyPriceDatabaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<SettingsSharedStore> {
        SettingsSharedStoreImpl(
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<UserSessionSharedStore> {
        UserSessionSharedStoreImpl(
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }

    single<OccupiedPeriodSharedCommandRepository> {
        OccupiedPeriodSharedCommandRepositoryImpl(
            databaseAdapter = get(),
            occupiedPeriodDatabaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<StayFileRepository> {
        StayFileRepositoryImpl(
            sharedStorageDataSource = get(),
        )
    }

    single<LeadTenantSharedCommandRepository> {
        TenantRepositoryProxy(
            leadTenantSharedCommandRepository = LeadTenantSharedCommandRepositoryImpl(
                databaseAdapter = get(),
                databaseDataSource = get(),
                plainPreferencesDataSource = get(plainPreferencesQualifier),
            ),
        )
    }

    single<UserCommandRepository> {
        UserCommandRepositoryImpl(
            remoteDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }

    // Stay details

    single<StayDetailsStayQueryRepository> {
        StayDetailsStayQueryRepositoryImpl(
            stayDatabaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<StayDetailsStayCommandRepository> {
        StayDetailsStayCommandRepositoryImpl(
            databaseAdapter = get(),
            stayDatabaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<StayDetailsPropertyQueryRepository> {
        StayDetailsPropertyQueryRepositoryImpl(
            propertyDatabaseDataSource = get(),
        )
    }

    single<StayDetailsTenantQueryRepository> {
        StayDetailsTenantQueryRepositoryImpl(
            tenantDatabaseDataSource = get(),
        )
    }

    single<StayDetailsAssistanceCommandRepository> {
        StayDetailsAssistanceCommandRepositoryImpl(
            databaseAdapter = get(),
            assistanceDatabaseDataSource = get(),
        )
    }

    // Property dashboard

    single<PropertyDetailsPropertyQueryRepository> {
        PropertyDetailsPropertyQueryRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<PropertyDashboardStayQueryRepository> {
        PropertyDashboardStayQueryRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<PropertyDashboardPropertyOwnerQueryRepository> {
        PropertyDashboardPropertyOwnerQueryRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    // Property occupation

    single<PropertyOccupationStayQueryRepository> {
        PropertyOccupationStayQueryRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    // Shared

    single<LeadTenantSharedQueryRepository> {
        LeadTenantSharedQueryRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<AppDateProvider> { AppDateProviderImpl() }

    // Stay registration

    single<StayRegistrationPropertyQueryRepository> {
        StayRegistrationPropertyQueryRepositoryImpl(databaseDataSource = get())
    }

    single<StayRegistrationStayQueryRepository> {
        StayRegistrationStayQueryRepositoryImpl(databaseDataSource = get())
    }

    single<StayRegistrationTenantQueryRepository> {
        StayRegistrationTenantQueryRepositoryImpl(databaseDataSource = get())
    }

    single<StayRegistrationStayCommandRepository> {
        StayRegistrationStayCommandRepositoryImpl(
            databaseAdapter = get(),
            stayDatabaseDataSource = get(),
            stayFileDatabaseDataSource = get(),
        )
    }

    // Lead tenant details

    single<LeadTenantDetailsLeadTenantQueryRepository> {
        LeadTenantDetailsLeadTenantQueryRepositoryImpl(
            tenantDatabaseDataSource = get(),
        )
    }

    single<LeadTenantDetailsLeadTenantCommandRepository> {
        LeadTenantDetailsLeadTenantCommandRepositoryImpl(
            databaseAdapter = get(),
            tenantDatabaseDataSource = get(),
        )
    }

    single<LeadTenantDetailsAssistanceQueryRepository> {
        LeadTenantDetailsAssistanceQueryRepositoryImpl(
            assistanceDatabaseDataSource = get(),
            propertyDatabaseDataSource = get(),
        )
    }

    single<LeadTenantDetailsAssistanceCommandRepository> {
        LeadTenantDetailsAssistanceCommandRepositoryImpl(
            databaseAdapter = get(),
            assistanceDatabaseDataSource = get(),
        )
    }

    single<LeadTenantDetailsAssistanceSentPropertyQueryRepository> {
        LeadTenantDetailsAssistanceSentPropertyQueryRepositoryImpl(
            propertyDatabaseDataSource = get(),
        )
    }

    single<LeadTenantDetailsStayQueryRepository> {
        LeadTenantDetailsStayQueryRepositoryImpl(
            stayDatabaseDataSource = get(),
        )
    }

    // Home

    single<HomeLeadTenantQueryRepository> {
        HomeLeadTenantQueryRepositoryImpl(
            tenantDatabaseDataSource = get(),
            stayDatabaseDataSource = get(),
            assistanceDatabaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<HomeLeadTenantCommandRepository> {
        HomeLeadTenantCommandRepositoryImpl(
            databaseAdapter = get(),
            tenantDatabaseDataSource = get(),
        )
    }

    single<HomeAssistanceCommandRepository> {
        HomeAssistanceCommandRepositoryImpl(
            databaseAdapter = get(),
            assistanceDatabaseDataSource = get(),
        )
    }
}
