package com.fstengineering.leasilycrm.occupiedPeriodDetails.di

import com.fstengineering.leasilycrm.core.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories.ImovelQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories.OccupiedPeriodQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories.TenantQueryRepositoryImpl
import org.koin.dsl.module

val occupiedPeriodDetailsRepositoryModule = module {
    single { get<AppDatabase>().occupiedPeriodDetailsClienteReadDataSource() }

    single<OccupiedPeriodQueryRepository> {
        OccupiedPeriodQueryRepositoryImpl(
            stayDatabaseDataSource = get(),
            plainLocalStorage = get(plainPreferencesQualifier),
        )
    }

    single<ImovelQueryRepository> {
        ImovelQueryRepositoryImpl(
            imovelDatabaseDataSource = get(),
        )
    }

    single<TenantQueryRepository> {
        TenantQueryRepositoryImpl(
            clienteReadDataSource = get(),
        )
    }
}
