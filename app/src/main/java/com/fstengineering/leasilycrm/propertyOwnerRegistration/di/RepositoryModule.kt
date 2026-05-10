package com.fstengineering.leasilycrm.propertyOwnerRegistration.di

import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories.PropertyOwnerCommandRepository
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyOwnerRegistration.infra.repositories.PropertyOwnerCommandRepositoryImpl
import com.fstengineering.leasilycrm.propertyOwnerRegistration.infra.repositories.PropertyOwnerQueryRepositoryImpl
import org.koin.dsl.module

val propertyOwnerRegistrationRepositoryModule = module {
    single<PropertyOwnerCommandRepository> {
        PropertyOwnerCommandRepositoryImpl(
            databaseAdapter = get(),
            propertyOwnerDatabaseDataSource = get(),
            propertyOwnerFileDatabaseDataSource = get(),
        )
    }

    single<PropertyOwnerQueryRepository> {
        PropertyOwnerQueryRepositoryImpl(databaseDataSource = get())
    }
}
