package com.fstengineering.leasilycrm.propertyOwnerDetails.di

import com.fstengineering.leasilycrm.propertyOwnerDetails.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyOwnerDetails.infra.repositories.PropertyOwnerQueryRepositoryImpl
import org.koin.dsl.module

val propertyOwnerDetailsRepositoryModule = module {
    single<PropertyOwnerQueryRepository> {
        PropertyOwnerQueryRepositoryImpl(databaseDataSource = get())
    }
}
