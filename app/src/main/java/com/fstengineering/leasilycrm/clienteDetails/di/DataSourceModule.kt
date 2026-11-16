package com.fstengineering.leasilycrm.clienteDetails.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val clienteDetailsDataSourceModule = module {
    single { get<AppDatabase>().clienteDetailsReadDataSource() }
}
