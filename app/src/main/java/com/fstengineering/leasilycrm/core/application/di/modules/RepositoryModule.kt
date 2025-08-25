package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.application.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.application.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.data.repositories.CustomerSegmentRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.CustomerSegmentTypeRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.DomainTablesRepositoryImpl
import com.fstengineering.leasilycrm.core.data.repositories.UserRepositoryImpl
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentRepository
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.DomainTablesRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
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

    single<UserRepository> {
        UserRepositoryImpl(
            api = get(),
            encryptedPreferencesStorage = get(encryptedPreferencesQualifier),
            plainPreferencesStorage = get(plainPreferencesQualifier),
        )
    }
}
