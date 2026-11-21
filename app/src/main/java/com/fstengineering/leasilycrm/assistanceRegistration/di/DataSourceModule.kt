package com.fstengineering.leasilycrm.assistanceRegistration.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val assistanceRegistrationDataSourceModule = module {
    single { get<AppDatabase>().assistanceWriteDataSource() }
}
