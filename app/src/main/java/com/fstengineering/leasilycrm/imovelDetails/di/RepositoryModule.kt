package com.fstengineering.leasilycrm.imovelDetails.di

import com.fstengineering.leasilycrm.imovelDetails.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.imovelDetails.infra.repositories.ImovelQueryRepositoryImpl
import org.koin.dsl.module

val imovelDetailsRepositoryModule = module {
    single<ImovelQueryRepository> {
        ImovelQueryRepositoryImpl(databaseDataSource = get())
    }
}
