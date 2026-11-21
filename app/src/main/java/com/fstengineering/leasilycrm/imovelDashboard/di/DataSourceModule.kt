package com.fstengineering.leasilycrm.imovelDashboard.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val imovelDashboardDataSourceModule = module {
    single { get<AppDatabase>().imovelDashboardReadDataSource() }
}
