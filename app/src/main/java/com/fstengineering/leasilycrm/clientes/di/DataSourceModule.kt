package com.fstengineering.leasilycrm.clientes.di

import com.fstengineering.leasilycrm.clientes.infra.localStorage.ClientesLocalStorage
import com.fstengineering.leasilycrm.core.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val clientesDataSourceModule = module {
    single { get<AppDatabase>().clientesReadDataSource() }
    single {
        ClientesLocalStorage(
            plainLocalStorage = get(
                plainPreferencesQualifier
            )
        )
    }
}
