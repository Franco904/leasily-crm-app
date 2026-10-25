package com.fstengineering.leasilycrm.proprietarioDetails.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val proprietarioDetailsDataSourceModule = module {
    single { get<AppDatabase>().proprietarioDetailsReadDataSource() }
}
