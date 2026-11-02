package com.fstengineering.leasilycrm.imoveis.di

import com.fstengineering.leasilycrm.imoveis.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.imoveis.infra.repositories.ImovelQueryRepositoryImpl
import org.koin.dsl.module

val imoveisRepositoryModule = module {
    single<ImovelQueryRepository> {
        ImovelQueryRepositoryImpl(databaseDataSource = get())
    }
}
