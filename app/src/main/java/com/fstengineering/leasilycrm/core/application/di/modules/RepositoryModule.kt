package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.application.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.application.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.application.models.AppDate
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceCharacteristicAssistanceRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceCharacteristicRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceInteractionRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceSearchedNeighborhoodRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceSentPropertyRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AuthRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.CrmMetricsRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.CustomerSegmentRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.CustomerSegmentTypeRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.DomainTablesRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyCharacteristicPropertyRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyCharacteristicRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyFileRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyOwnerFileRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyOwnerRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyPriceRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertySeaProximityRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.PropertyTypeRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.SettingsRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.StayFileRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.StayRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.StayTransferRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.TenantContactOriginTypeRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.TenantRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.UserRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.proxies.TenantRepositoryProxy
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceCharacteristicAssistanceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceInteractionRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceSearchedNeighborhoodRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceSentPropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AuthRepository
import com.fstengineering.leasilycrm.core.domain.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentRepository
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.DomainTablesRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyCharacteristicPropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyPriceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertySeaProximityTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.SettingsRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayTransferRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantContactOriginTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AssistanceCharacteristicAssistanceRepository> {
        AssistanceCharacteristicAssistanceRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<AssistanceCharacteristicRepository> {
        AssistanceCharacteristicRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<AssistanceRepository> {
        AssistanceRepositoryImpl(
            databaseDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }

    single<AssistanceInteractionRepository> {
        AssistanceInteractionRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<AssistanceSearchedNeighborhoodRepository> {
        AssistanceSearchedNeighborhoodRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<AssistanceSentPropertyRepository> {
        AssistanceSentPropertyRepositoryImpl(
            databaseDataSource = get(),
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

    single<CustomerSegmentRepository> {
        CustomerSegmentRepositoryImpl(
            databaseDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }

    single<CustomerSegmentTypeRepository> {
        CustomerSegmentTypeRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<DomainTablesRepository> {
        DomainTablesRepositoryImpl(
            connectivityAdapter = get(),
            remoteDataSource = get(),
            assistanceCharacteristicDataSource = get(),
            customerSegmentTypeDataSource = get(),
            propertyCharacteristicDataSource = get(),
            propertySeaProximityTypeDataSource = get(),
            propertyTypeDataSource = get(),
            tenantContactOriginTypeDataSource = get(),
        )
    }

    single<PropertyCharacteristicPropertyRepository> {
        PropertyCharacteristicPropertyRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<PropertyCharacteristicRepository> {
        PropertyCharacteristicRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<PropertyFileRepository> {
        PropertyFileRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<PropertyOwnerFileRepository> {
        PropertyOwnerFileRepositoryImpl(
            databaseDataSource = get(),
            sharedStorageDataSource = get(),
        )
    }

    single<PropertyOwnerRepository> {
        PropertyOwnerRepositoryImpl(
            databaseDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
            sharedStorageDataSource = get(),
        )
    }

    single<PropertyPriceRepository> {
        PropertyPriceRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<PropertyRepository> {
        PropertyRepositoryImpl(
            databaseDataSource = get(),
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
            databaseDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
            sharedStorageDataSource = get(),
        )
    }

    single<StayFileRepository> {
        StayFileRepositoryImpl(
            databaseDataSource = get(),
            sharedStorageDataSource = get(),
        )
    }

    single<StayTransferRepository> {
        StayTransferRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<TenantRepository> {
        val realRepository = TenantRepositoryImpl(
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
