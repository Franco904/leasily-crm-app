package com.fstengineering.leasilycrm.proprietarios.di

import com.fstengineering.leasilycrm.proprietarios.application.repositories.ProprietarioQueryRepository
import com.fstengineering.leasilycrm.proprietarios.infra.repositories.ProprietarioQueryRepositoryImpl
import org.koin.dsl.module

val proprietariosRepositoryModule = module {
    single<ProprietarioQueryRepository> {
        ProprietarioQueryRepositoryImpl(databaseDataSource = get())
    }
}
