package com.fstengineering.leasilycrm.propertyOccupation.di

import com.fstengineering.leasilycrm.propertyOccupation.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.propertyOccupation.infra.repositories.OccupiedPeriodQueryRepositoryImpl
import org.koin.dsl.module

val propertyOccupationRepositoryModule = module {
    single<OccupiedPeriodQueryRepository> {
        OccupiedPeriodQueryRepositoryImpl(databaseDataSource = get())
    }
}
