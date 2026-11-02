package com.fstengineering.leasilycrm.imovelOccupation.di

import com.fstengineering.leasilycrm.imovelOccupation.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.imovelOccupation.infra.repositories.OccupiedPeriodQueryRepositoryImpl
import org.koin.dsl.module

val imovelOccupationRepositoryModule = module {
    single<OccupiedPeriodQueryRepository> {
        OccupiedPeriodQueryRepositoryImpl(databaseDataSource = get())
    }
}
