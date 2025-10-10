package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.application.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.application.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.application.models.AppLazyDate
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceCharacteristicAssistanceRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceCharacteristicRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceInteractionRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceSearchedNeighborhoodRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.AssistanceSentPropertyRepositoryImpl
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
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceCharacteristicAssistanceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceInteractionRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceSearchedNeighborhoodRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceSentPropertyRepository
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
            dao = get(),
        )
    }

    single<AssistanceCharacteristicRepository> {
        AssistanceCharacteristicRepositoryImpl(
            dao = get(),
        )
    }

    single<AssistanceRepository> {
        AssistanceRepositoryImpl(
            dao = get(),
            encryptedPreferencesStorage = get(encryptedPreferencesQualifier),
        )
    }

    single<AssistanceInteractionRepository> {
        AssistanceInteractionRepositoryImpl(
            dao = get(),
        )
    }

    single<AssistanceSearchedNeighborhoodRepository> {
        AssistanceSearchedNeighborhoodRepositoryImpl(
            dao = get(),
        )
    }

    single<AssistanceSentPropertyRepository> {
        AssistanceSentPropertyRepositoryImpl(
            dao = get(),
        )
    }

    single<CustomerSegmentRepository> {
        CustomerSegmentRepositoryImpl(
            dao = get(),
            encryptedPreferencesStorage = get(encryptedPreferencesQualifier),
        )
    }

    single<CustomerSegmentTypeRepository> {
        CustomerSegmentTypeRepositoryImpl(
            dao = get(),
        )
    }

    single<DomainTablesRepository> {
        DomainTablesRepositoryImpl(
            api = get(),
            assistanceCharacteristicDao = get(),
            customerSegmentTypeDao = get(),
            propertyCharacteristicDao = get(),
            propertySeaProximityTypeDao = get(),
            propertyTypeDao = get(),
            tenantContactOriginTypeDao = get(),
        )
    }

    single<PropertyCharacteristicPropertyRepository> {
        PropertyCharacteristicPropertyRepositoryImpl(
            dao = get(),
        )
    }

    single<PropertyCharacteristicRepository> {
        PropertyCharacteristicRepositoryImpl(
            dao = get(),
        )
    }

    single<PropertyFileRepository> {
        PropertyFileRepositoryImpl(
            dao = get(),
        )
    }

    single<PropertyOwnerFileRepository> {
        PropertyOwnerFileRepositoryImpl(
            dao = get(),
            sharedStorage = get(),
        )
    }

    single<PropertyOwnerRepository> {
        PropertyOwnerRepositoryImpl(
            dao = get(),
            encryptedPreferencesStorage = get(encryptedPreferencesQualifier),
            sharedStorage = get(),
        )
    }

    single<PropertyPriceRepository> {
        PropertyPriceRepositoryImpl(
            dao = get(),
        )
    }

    single<PropertyRepository> {
        PropertyRepositoryImpl(
            dao = get(),
            encryptedPreferencesStorage = get(encryptedPreferencesQualifier),
            plainPreferencesStorage = get(plainPreferencesQualifier),
            now = AppLazyDate.now(),
        )
    }

    single<PropertySeaProximityTypeRepository> {
        PropertySeaProximityRepositoryImpl(
            dao = get(),
        )
    }

    single<PropertyTypeRepository> {
        PropertyTypeRepositoryImpl(
            dao = get(),
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            plainPreferencesStorage = get(plainPreferencesQualifier),
        )
    }

    single<StayRepository> {
        StayRepositoryImpl(
            dao = get(),
            encryptedPreferencesStorage = get(encryptedPreferencesQualifier),
            plainPreferencesStorage = get(plainPreferencesQualifier),
            sharedStorage = get(),
        )
    }

    single<StayFileRepository> {
        StayFileRepositoryImpl(
            dao = get(),
            sharedStorage = get(),
        )
    }

    single<StayTransferRepository> {
        StayTransferRepositoryImpl(
            dao = get(),
        )
    }

    single<TenantRepository> {
        TenantRepositoryImpl(
            dao = get(),
            encryptedPreferencesStorage = get(encryptedPreferencesQualifier),
            plainPreferencesStorage = get(plainPreferencesQualifier),
        )
    }

    single<TenantContactOriginTypeRepository> {
        TenantContactOriginTypeRepositoryImpl(
            dao = get(),
        )
    }

    single<UserRepository> {
        UserRepositoryImpl(
            api = get(),
            encryptedPreferencesStorage = get(encryptedPreferencesQualifier),
            plainPreferencesStorage = get(plainPreferencesQualifier),
        )
    }
}
