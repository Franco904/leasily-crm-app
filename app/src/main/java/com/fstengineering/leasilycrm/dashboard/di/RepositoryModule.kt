package com.fstengineering.leasilycrm.dashboard.di

import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.dashboard.application.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.dashboard.infra.repositories.CrmMetricsRepositoryImpl
import org.koin.dsl.module

val dashboardRepositoryModule = module {
    single<CrmMetricsRepository> {
        CrmMetricsRepositoryImpl(
            databaseDataSource = get(),
            dateProvider = get<AppDateProvider>(),
        )
    }
}
