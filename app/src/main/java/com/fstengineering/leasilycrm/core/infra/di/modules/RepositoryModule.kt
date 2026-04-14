package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSentPropertyQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceCommandRepositoryImpl
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceQueryRepositoryImpl
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceSentPropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.core.application.gateways.UserSessionSharedGateway
import com.fstengineering.leasilycrm.core.application.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedQueryRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertyOwnerSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.StayFileRepository
import com.fstengineering.leasilycrm.core.application.repositories.StaySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.stores.SettingsSharedStore
import com.fstengineering.leasilycrm.core.application.stores.UserSessionSharedStore
import com.fstengineering.leasilycrm.core.domain.providers.AppDateProviderImpl
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.gateways.UserSessionSharedGatewayImpl
import com.fstengineering.leasilycrm.core.infra.repositories.CrmMetricsRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.LeadTenantSharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.LeadTenantSharedQueryRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.PropertyOwnerSharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.PropertySharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.StayFileRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.StaySharedCommandRepositoryImpl
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
import com.fstengineering.leasilycrm.propertyOccupation.application.repositories.StayQueryRepository as PropertyOccupationStayQueryRepository
import com.fstengineering.leasilycrm.propertyOccupation.infra.repositories.StayQueryRepositoryImpl as PropertyOccupationStayQueryRepositoryImpl
import com.fstengineering.leasilycrm.signin.application.gateways.UserSessionGateway
import com.fstengineering.leasilycrm.signin.infra.gateways.UserSessionGatewayImpl
import com.fstengineering.leasilycrm.stayDetails.application.repositories.AssistanceCommandRepository as StayDetailsAssistanceCommandRepository
import com.fstengineering.leasilycrm.stayDetails.application.repositories.PropertyQueryRepository as StayDetailsPropertyQueryRepository
import com.fstengineering.leasilycrm.stayDetails.application.repositories.OccupiedPeriodCommandRepository as StayDetailsStayCommandRepository
import com.fstengineering.leasilycrm.stayDetails.application.repositories.OccupiedPeriodQueryRepository as StayDetailsStayQueryRepository
import com.fstengineering.leasilycrm.stayDetails.application.repositories.TenantQueryRepository as StayDetailsTenantQueryRepository
import com.fstengineering.leasilycrm.stayDetails.infra.repositories.AssistanceCommandRepositoryImpl as StayDetailsAssistanceCommandRepositoryImpl
import com.fstengineering.leasilycrm.stayDetails.infra.repositories.PropertyQueryRepositoryImpl as StayDetailsPropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.stayDetails.infra.repositories.OccupiedPeriodCommandRepositoryImpl as StayDetailsStayCommandRepositoryImpl
import com.fstengineering.leasilycrm.stayDetails.infra.repositories.OccupiedPeriodQueryRepositoryImpl as StayDetailsStayQueryRepositoryImpl
import com.fstengineering.leasilycrm.stayDetails.infra.repositories.TenantQueryRepositoryImpl as StayDetailsTenantQueryRepositoryImpl
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.PropertyQueryRepository as StayRegistrationPropertyQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.StayCommandRepository as StayRegistrationStayCommandRepository
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.StayQueryRepository as StayRegistrationStayQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.TenantQueryRepository as StayRegistrationTenantQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.infra.repositories.PropertyQueryRepositoryImpl as StayRegistrationPropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.stayRegistration.infra.repositories.StayCommandRepositoryImpl as StayRegistrationStayCommandRepositoryImpl
import com.fstengineering.leasilycrm.stayRegistration.infra.repositories.StayQueryRepositoryImpl as StayRegistrationStayQueryRepositoryImpl
import com.fstengineering.leasilycrm.stayRegistration.infra.repositories.TenantQueryRepositoryImpl as StayRegistrationTenantQueryRepositoryImpl
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
        CrmMetricsRepositoryImpl(
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

    single<StaySharedCommandRepository> {
        StaySharedCommandRepositoryImpl(
            databaseAdapter = get(),
            stayDatabaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<StayFileRepository> {
        StayFileRepositoryImpl(
            sharedStorageDataSource = get(),
        )
    }

    single<LeadTenantSharedCommandRepository> {
        val realRepository = LeadTenantSharedCommandRepositoryImpl(
            databaseAdapter = get(),
            databaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )

        TenantRepositoryProxy(
            tenantInMemoryDataSource = get(),
            leadTenantSharedCommandRepository = realRepository,
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
}
