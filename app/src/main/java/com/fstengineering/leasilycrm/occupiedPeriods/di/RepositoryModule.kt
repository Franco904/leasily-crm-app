package com.fstengineering.leasilycrm.occupiedPeriods.di

import com.fstengineering.leasilycrm.core.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.occupiedPeriods.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriods.infra.repositories.OccupiedPeriodQueryRepositoryImpl
import org.koin.dsl.module

val occupiedPeriodsRepositoryModule = module {
    single<OccupiedPeriodQueryRepository> {
        OccupiedPeriodQueryRepositoryImpl(
            occupiedPeriodDatabaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }
}
