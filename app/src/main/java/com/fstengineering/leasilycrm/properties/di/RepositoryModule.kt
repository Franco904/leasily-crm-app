package com.fstengineering.leasilycrm.properties.di

import com.fstengineering.leasilycrm.properties.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.properties.infra.repositories.PropertyQueryRepositoryImpl
import org.koin.dsl.module

val propertiesRepositoryModule = module {
    single<PropertyQueryRepository> {
        PropertyQueryRepositoryImpl(databaseDataSource = get())
    }
}
