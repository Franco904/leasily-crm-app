package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.core.infra.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AuthRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.CrmMetricsRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyOwnerFileRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyOwnerRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.SettingsRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.StayFileRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.StayRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.TenantRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.UserRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.proxies.TenantRepositoryProxy
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AuthRepository
import com.fstengineering.leasilycrm.core.domain.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.SettingsRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AssistanceRepository> {
        AssistanceRepositoryImpl(
            databaseAdapter = get(),
            assistanceDatabaseDataSource = get(),
            assistanceCharacteristicCrossRefDatabaseDataSource = get(),
            assistanceInteractionDatabaseDataSource = get(),
            assistanceSearchedNeighborhoodDatabaseDataSource = get(),
            assistanceSentPropertyCrossRefDatabaseDataSource = get(),
        )
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
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

    single<UserRepository> {
        UserRepositoryImpl(
            remoteDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
            userSessionService = get(),
        )
    }
}
