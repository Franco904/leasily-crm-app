package com.fstengineering.leasilycrm.atendimentoRegistration.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val atendimentoRegistrationDataSourceModule = module {
    single { get<AppDatabase>().atendimentoWriteDataSource() }
    single { get<AppDatabase>().atendimentoRegistrationReadDataSource() }
    single { get<AppDatabase>().atendimentoRegistrationImovelReadDataSource() }
}
