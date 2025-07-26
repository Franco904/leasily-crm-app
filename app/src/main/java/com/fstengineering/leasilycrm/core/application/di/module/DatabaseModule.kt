package com.fstengineering.leasilycrm.core.application.di.module

import com.fstengineering.leasilycrm.core.data.dataSources.database.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single<AppDatabase> { AppDatabase.buildDatabase(appContext = get()) }
}
