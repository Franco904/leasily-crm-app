package com.fstengineering.leasilycrm.imovelRegistration.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val imovelRegistrationDataSourceModule = module {
    single { get<AppDatabase>().imovelWriteDataSource() }
    single { get<AppDatabase>().imovelRegistrationProprietarioReadDataSource() }
}
