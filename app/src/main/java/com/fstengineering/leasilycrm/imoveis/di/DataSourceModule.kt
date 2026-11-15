package com.fstengineering.leasilycrm.imoveis.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val imoveisDataSourceModule = module {
    single { get<AppDatabase>().imoveisReadDataSource() }
}
