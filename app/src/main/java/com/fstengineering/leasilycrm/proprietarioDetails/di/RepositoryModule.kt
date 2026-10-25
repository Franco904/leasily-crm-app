package com.fstengineering.leasilycrm.proprietarioDetails.di

import com.fstengineering.leasilycrm.proprietarioDetails.application.repositories.ProprietarioQueryRepository
import com.fstengineering.leasilycrm.proprietarioDetails.infra.repositories.ProprietarioQueryRepositoryImpl
import org.koin.dsl.module

val proprietarioDetailsRepositoryModule = module {
    single<ProprietarioQueryRepository> {
        ProprietarioQueryRepositoryImpl(databaseDataSource = get())
    }
}
