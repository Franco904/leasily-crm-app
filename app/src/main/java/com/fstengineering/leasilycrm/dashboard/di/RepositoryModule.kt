package com.fstengineering.leasilycrm.dashboard.di

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.dashboard.application.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.dashboard.infra.repositories.CrmMetricsRepositoryImpl
import org.koin.dsl.module

val dashboardRepositoryModule = module {
    single<CrmMetricsRepository> {
        CrmMetricsRepositoryImpl(
            databaseDataSource = get(),
            now = AppDate.lazyNow(),
        )
    }
}
