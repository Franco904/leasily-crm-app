package com.fstengineering.leasilycrm.occupiedPeriodRegistration.di

import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodCommandRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories.OccupiedPeriodCommandRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories.OccupiedPeriodQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories.ImovelQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories.TenantQueryRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import org.koin.dsl.module

val occupiedPeriodRegistrationRepositoryModule = module {
    single { get<AppDatabase>().occupiedPeriodRegistrationClienteReadDataSource() }

    single<ImovelQueryRepository> {
        ImovelQueryRepositoryImpl(databaseDataSource = get())
    }

    single<OccupiedPeriodQueryRepository> {
        OccupiedPeriodQueryRepositoryImpl(databaseDataSource = get())
    }

    single<TenantQueryRepository> {
        TenantQueryRepositoryImpl(clienteReadDataSource = get())
    }

    single<OccupiedPeriodCommandRepository> {
        OccupiedPeriodCommandRepositoryImpl(
            databaseAdapter = get(),
            stayDatabaseDataSource = get(),
            stayFileDatabaseDataSource = get(),
        )
    }
}
