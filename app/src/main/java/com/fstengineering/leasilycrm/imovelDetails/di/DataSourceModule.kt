package com.fstengineering.leasilycrm.imovelDetails.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val imovelDetailsDataSourceModule = module {
    single { get<AppDatabase>().imovelDetailsReadDataSource() }
}
