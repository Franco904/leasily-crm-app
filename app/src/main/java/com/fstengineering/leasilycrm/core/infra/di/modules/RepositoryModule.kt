package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSentPropertyQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceCommandRepositoryImpl
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceQueryRepositoryImpl
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceSentPropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.core.application.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertyOwnerFileRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertyRepository
import com.fstengineering.leasilycrm.settings.application.repositories.SettingsRepository
import com.fstengineering.leasilycrm.core.application.repositories.StayFileRepository
import com.fstengineering.leasilycrm.core.application.repositories.StayRepository
import com.fstengineering.leasilycrm.core.application.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.application.repositories.UserRetryInfoCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.UserSessionRepository
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.repositories.CrmMetricsRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.PropertyOwnerFileRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.PropertyOwnerRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.PropertyRepositoryImpl
import com.fstengineering.leasilycrm.settings.infra.repositories.SettingsRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.StayFileRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.StayRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.TenantRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.UserRetryInfoCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.UserSessionRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.proxies.TenantRepositoryProxy
import com.fstengineering.leasilycrm.profile.application.repositories.UserProfileCommandRepository
import com.fstengineering.leasilycrm.profile.application.repositories.UserProfileQueryRepository
import com.fstengineering.leasilycrm.profile.infra.repositories.UserProfileCommandRepositoryImpl
import com.fstengineering.leasilycrm.profile.infra.repositories.UserProfileQueryRepositoryImpl
import com.fstengineering.leasilycrm.signin.application.repositories.SignInRepository
import com.fstengineering.leasilycrm.signin.application.repositories.UserSignInCommandRepository
import com.fstengineering.leasilycrm.signin.infra.repositories.SignInRepositoryImpl
import com.fstengineering.leasilycrm.signin.infra.repositories.UserSignInCommandRepositoryImpl
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

    single<UserProfileCommandRepository> {
        UserProfileCommandRepositoryImpl(
            remoteDataSource = get(),
            encryptedPreferencesDataSource = get(),
        )
    }

    single<UserProfileQueryRepository> {
        UserProfileQueryRepositoryImpl(
            encryptedPreferencesDataSource = get(),
        )
    }

    // Sign in

    single<SignInRepository> {
        SignInRepositoryImpl(
            remoteDataSource = get(),
        )
    }

    single<UserSignInCommandRepository> {
        UserSignInCommandRepositoryImpl(
            encryptedPreferencesDataSource = get(),
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

    single<PropertyOwnerFileRepository> {
        PropertyOwnerFileRepositoryImpl(
            sharedStorageDataSource = get(),
        )
    }

    single<PropertyOwnerRepository> {
        PropertyOwnerRepositoryImpl(
            databaseAdapter = get(),
            propertyOwnerDatabaseDataSource = get(),
            propertyOwnerFileDatabaseDataSource = get(),
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

    single<SettingsRepository> {
        SettingsRepositoryImpl(
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
