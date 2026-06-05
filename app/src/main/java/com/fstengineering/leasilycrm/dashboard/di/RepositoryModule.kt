package com.fstengineering.leasilycrm.dashboard.di

import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.dashboard.application.repositories.UserAnalyticsRepository
import com.fstengineering.leasilycrm.dashboard.infra.repositories.UserAnalyticsRepositoryImpl
import org.koin.dsl.module

val dashboardRepositoryModule = module {
    single<UserAnalyticsRepository> {
        UserAnalyticsRepositoryImpl(
            databaseDataSource = get(),
            dateProvider = get<AppDateProvider>(),
        )
    }
}
