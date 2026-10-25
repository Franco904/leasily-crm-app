package com.fstengineering.leasilycrm.propertyDashboard.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val propertyDashboardDataSourceModule = module {
    single { get<AppDatabase>().propertyDashboardProprietarioReadDataSource() }
}
