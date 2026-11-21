package com.fstengineering.leasilycrm.clienteRegistration.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val clienteRegistrationDataSourceModule = module {
    single { get<AppDatabase>().clienteWriteDataSource() }
    single { get<AppDatabase>().clienteRegistrationReadDataSource() }
}
