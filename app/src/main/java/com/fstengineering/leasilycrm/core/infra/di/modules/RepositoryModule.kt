package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSentPropertyQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceCommandRepositoryImpl
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceQueryRepositoryImpl
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceSentPropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.core.application.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertyOwnerSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.application.repositories.SettingsSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.SettingsSharedQueryRepository
import com.fstengineering.leasilycrm.core.application.repositories.StayFileRepository
import com.fstengineering.leasilycrm.core.application.repositories.StayRepository
import com.fstengineering.leasilycrm.core.application.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.application.repositories.UserRetryInfoCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.UserSessionRepository
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.repositories.CrmMetricsRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.PropertyOwnerSharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.PropertyRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.SettingsSharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.SettingsSharedQueryRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.StayFileRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.StayRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.TenantRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.UserRetryInfoCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.UserSessionRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.proxies.TenantRepositoryProxy
import com.fstengineering.leasilycrm.profile.application.repositories.UserCommandRepository
import com.fstengineering.leasilycrm.profile.application.repositories.UserQueryRepository
import com.fstengineering.leasilycrm.profile.infra.repositories.UserCommandRepositoryImpl
import com.fstengineering.leasilycrm.profile.infra.repositories.UserQueryRepositoryImpl
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories.PropertyOwnerCommandRepository
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyOwnerRegistration.infra.repositories.PropertyOwnerCommandRepositoryImpl
import com.fstengineering.leasilycrm.propertyOwnerRegistration.infra.repositories.PropertyOwnerQueryRepositoryImpl
import com.fstengineering.leasilycrm.signin.application.repositories.SignInRepository
import com.fstengineering.leasilycrm.signin.infra.repositories.SignInRepositoryImpl
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

    // Profile

    single<UserCommandRepository> {
        UserCommandRepositoryImpl(
            remoteDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }

    single<UserQueryRepository> {
        UserQueryRepositoryImpl(
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
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
            propertyOwnerDatabaseDataSource = get(),
        )
    }

    // Sign in

    single<SignInRepository> {
        SignInRepositoryImpl(
            remoteDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }

    // Core

    single<UserSessionRepository> {
        UserSessionRepositoryImpl(
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

    single<PropertyRepository> {
        PropertyRepositoryImpl(
            databaseAdapter = get(),
            propertyDatabaseDataSource = get(),
            propertyCharacteristicCrossRefDatabaseDataSource = get(),
            propertyFileDatabaseDataSource = get(),
            propertyPriceDatabaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<SettingsSharedCommandRepository> {
        SettingsSharedCommandRepositoryImpl(
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<SettingsSharedQueryRepository> {
        SettingsSharedQueryRepositoryImpl(
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<StayRepository> {
        StayRepositoryImpl(
            databaseAdapter = get(),
            stayDatabaseDataSource = get(),
            stayFileDatabaseDataSource = get(),
            stayTransferDatabaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<StayFileRepository> {
        StayFileRepositoryImpl(
            sharedStorageDataSource = get(),
        )
    }

    single<TenantRepository> {
        val realRepository = TenantRepositoryImpl(
            databaseAdapter = get(),
            databaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )

        TenantRepositoryProxy(
            tenantInMemoryDataSource = get(),
            tenantRepository = realRepository,
        )
    }

    single<UserRetryInfoCommandRepository> {
        UserRetryInfoCommandRepositoryImpl(
            remoteDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }
}
