package com.fstengineering.leasilycrm.proprietarios.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val proprietariosDataSourceModule = module {
    single { get<AppDatabase>().proprietariosReadDataSource() }
}
