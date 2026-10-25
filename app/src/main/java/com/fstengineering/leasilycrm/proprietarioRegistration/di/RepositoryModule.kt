package com.fstengineering.leasilycrm.proprietarioRegistration.di

import com.fstengineering.leasilycrm.proprietarioRegistration.application.repositories.ProprietarioCommandRepository
import com.fstengineering.leasilycrm.proprietarioRegistration.application.repositories.ProprietarioQueryRepository
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.repositories.ProprietarioCommandRepositoryImpl
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.repositories.ProprietarioQueryRepositoryImpl
import org.koin.dsl.module

val proprietarioRegistrationRepositoryModule = module {
    single<ProprietarioCommandRepository> {
        ProprietarioCommandRepositoryImpl(
            databaseAdapter = get(),
            proprietarioDatabaseDataSource = get(),
            proprietarioFileDatabaseDataSource = get(),
        )
    }

    single<ProprietarioQueryRepository> {
        ProprietarioQueryRepositoryImpl(databaseDataSource = get())
    }
}
