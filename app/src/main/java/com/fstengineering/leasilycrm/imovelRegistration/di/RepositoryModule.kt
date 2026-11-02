package com.fstengineering.leasilycrm.imovelRegistration.di

import com.fstengineering.leasilycrm.imovelRegistration.application.repositories.ImovelCommandRepository
import com.fstengineering.leasilycrm.imovelRegistration.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.imovelRegistration.infra.repositories.ImovelCommandRepositoryImpl
import com.fstengineering.leasilycrm.imovelRegistration.infra.repositories.ImovelQueryRepositoryImpl
import org.koin.dsl.module

val imovelRegistrationRepositoryModule = module {
    single<ImovelQueryRepository> {
        ImovelQueryRepositoryImpl(imovelDatabaseDataSource = get())
    }

    single<ImovelCommandRepository> {
        ImovelCommandRepositoryImpl(
            databaseAdapter = get(),
            imovelDatabaseDataSource = get(),
            imovelCharacteristicCrossRefDatabaseDataSource = get(),
            imovelPriceDatabaseDataSource = get(),
            imovelFileDatabaseDataSource = get(),
        )
    }
}
