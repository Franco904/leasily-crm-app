package com.fstengineering.leasilycrm.propertyDetails.di

import com.fstengineering.leasilycrm.propertyDetails.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.propertyDetails.infra.repositories.PropertyQueryRepositoryImpl
import org.koin.dsl.module

val propertyDetailsRepositoryModule = module {
    single<PropertyQueryRepository> {
        PropertyQueryRepositoryImpl(databaseDataSource = get())
    }
}
