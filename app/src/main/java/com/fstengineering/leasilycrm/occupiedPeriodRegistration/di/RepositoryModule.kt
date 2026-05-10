package com.fstengineering.leasilycrm.occupiedPeriodRegistration.di

import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodCommandRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories.OccupiedPeriodCommandRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories.OccupiedPeriodQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories.PropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories.TenantQueryRepositoryImpl
import org.koin.dsl.module

val occupiedPeriodRegistrationRepositoryModule = module {
    single<PropertyQueryRepository> {
        PropertyQueryRepositoryImpl(databaseDataSource = get())
    }

    single<OccupiedPeriodQueryRepository> {
        OccupiedPeriodQueryRepositoryImpl(databaseDataSource = get())
    }

    single<TenantQueryRepository> {
        TenantQueryRepositoryImpl(databaseDataSource = get())
    }

    single<OccupiedPeriodCommandRepository> {
        OccupiedPeriodCommandRepositoryImpl(
            databaseAdapter = get(),
            stayDatabaseDataSource = get(),
            stayFileDatabaseDataSource = get(),
        )
    }
}
