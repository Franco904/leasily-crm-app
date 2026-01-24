package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.application.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.application.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.application.models.AppDate
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceCharacteristicRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AuthRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.CrmMetricsRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.DomainTablesRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyCharacteristicRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyOwnerFileRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyOwnerRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertySeaProximityRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyTypeRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.SettingsRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.StayFileRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.StayRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.TenantContactOriginTypeRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.TenantRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.UserRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.proxies.TenantRepositoryProxy
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AuthRepository
import com.fstengineering.leasilycrm.core.domain.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.domain.repositories.DomainTablesRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertySeaProximityTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.SettingsRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantContactOriginTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AssistanceCharacteristicRepository> {
        AssistanceCharacteristicRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<AssistanceRepository> {
        AssistanceRepositoryImpl(
            databaseAdapter = get(),
            assistanceDatabaseDataSource = get(),
            assistanceCharacteristicCrossRefDatabaseDataSource = get(),
            assistanceInteractionDatabaseDataSource = get(),
            assistanceSearchedNeighborhoodDatabaseDataSource = get(),
            assistanceSentPropertyCrossRefDatabaseDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
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
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
            now = AppDate.lazyNow(),
        )
    }

    single<DomainTablesRepository> {
        DomainTablesRepositoryImpl(
            databaseAdapter = get(),
            assistanceCharacteristicDataSource = get(),
            propertyCharacteristicDataSource = get(),
            propertySeaProximityTypeDataSource = get(),
            propertyTypeDataSource = get(),
            tenantContactOriginTypeDataSource = get(),
            connectivityAdapter = get(),
            remoteDataSource = get(),
        )
    }

    single<PropertyCharacteristicRepository> {
        PropertyCharacteristicRepositoryImpl(
            databaseDataSource = get(),
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
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
            sharedStorageDataSource = get(),
        )
    }

    single<PropertyRepository> {
        PropertyRepositoryImpl(
            databaseAdapter = get(),
            propertyDatabaseDataSource = get(),
            propertyCharacteristicPropertyDatabaseDataSource = get(),
            propertyFileDatabaseDataSource = get(),
            propertyPriceDatabaseDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<PropertySeaProximityTypeRepository> {
        PropertySeaProximityRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<PropertyTypeRepository> {
        PropertyTypeRepositoryImpl(
            databaseDataSource = get(),
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
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
            sharedStorageDataSource = get(),
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
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )

        TenantRepositoryProxy(
            tenantInMemoryDataSource = get(),
            tenantRepository = realRepository,
        )
    }

    single<TenantContactOriginTypeRepository> {
        TenantContactOriginTypeRepositoryImpl(
            databaseDataSource = get(),
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
