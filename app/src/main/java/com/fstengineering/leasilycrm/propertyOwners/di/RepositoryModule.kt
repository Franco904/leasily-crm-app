package com.fstengineering.leasilycrm.propertyOwners.di

import com.fstengineering.leasilycrm.propertyOwners.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyOwners.infra.repositories.PropertyOwnerQueryRepositoryImpl
import org.koin.dsl.module

val propertyOwnersRepositoryModule = module {
    single<PropertyOwnerQueryRepository> {
        PropertyOwnerQueryRepositoryImpl(databaseDataSource = get())
    }
}
