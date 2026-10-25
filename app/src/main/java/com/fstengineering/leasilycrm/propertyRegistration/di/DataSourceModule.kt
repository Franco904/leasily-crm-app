package com.fstengineering.leasilycrm.propertyRegistration.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val propertyRegistrationDataSourceModule = module {
    single { get<AppDatabase>().propertyRegistrationProprietarioReadDataSource() }
}
