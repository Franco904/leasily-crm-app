package com.fstengineering.leasilycrm.propertyRegistration.di

import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.PropertyCommandRepository
import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.ProprietarioQueryRepository
import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.propertyRegistration.infra.repositories.PropertyCommandRepositoryImpl
import com.fstengineering.leasilycrm.propertyRegistration.infra.repositories.ProprietarioQueryRepositoryImpl
import com.fstengineering.leasilycrm.propertyRegistration.infra.repositories.PropertyQueryRepositoryImpl
import org.koin.dsl.module

val propertyRegistrationRepositoryModule = module {
    single<PropertyQueryRepository> {
        PropertyQueryRepositoryImpl(propertyDatabaseDataSource = get())
    }

    single<ProprietarioQueryRepository> {
        ProprietarioQueryRepositoryImpl(databaseDataSource = get())
    }

    single<PropertyCommandRepository> {
        PropertyCommandRepositoryImpl(
            databaseAdapter = get(),
            propertyDatabaseDataSource = get(),
            propertyCharacteristicCrossRefDatabaseDataSource = get(),
            propertyPriceDatabaseDataSource = get(),
            propertyFileDatabaseDataSource = get(),
        )
    }
}
