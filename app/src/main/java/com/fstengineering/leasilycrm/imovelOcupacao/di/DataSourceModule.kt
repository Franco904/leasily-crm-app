package com.fstengineering.leasilycrm.imovelOcupacao.di

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val imovelOcupacaoDataSourceModule = module {
    single { get<AppDatabase>().imovelOcupacaoReadDataSource() }
}
