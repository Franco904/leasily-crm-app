package com.fstengineering.leasilycrm.imovelDashboard.di

import com.fstengineering.leasilycrm.imovelDashboard.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.imovelDashboard.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.imovelDashboard.infra.repositories.ImovelQueryRepositoryImpl
import com.fstengineering.leasilycrm.imovelDashboard.infra.repositories.StayQueryRepositoryImpl
import org.koin.dsl.module

val imovelDashboardRepositoryModule = module {
    single<StayQueryRepository> {
        StayQueryRepositoryImpl(databaseDataSource = get())
    }

    single<ImovelQueryRepository> {
        ImovelQueryRepositoryImpl(databaseDataSource = get())
    }
}
