package com.fstengineering.leasilycrm.proprietarioRegistration.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val proprietarioRegistrationDataSourceModule = module {
    single { get<AppDatabase>().proprietarioWriteDataSource() }
    single { get<AppDatabase>().proprietarioRegistrationReadDataSource() }
}
