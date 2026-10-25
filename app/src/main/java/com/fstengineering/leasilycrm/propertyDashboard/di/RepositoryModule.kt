package com.fstengineering.leasilycrm.propertyDashboard.di

import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.infra.repositories.PropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.propertyDashboard.infra.repositories.StayQueryRepositoryImpl
import org.koin.dsl.module

val propertyDashboardRepositoryModule = module {
    single<StayQueryRepository> {
        StayQueryRepositoryImpl(databaseDataSource = get())
    }

    single<PropertyQueryRepository> {
        PropertyQueryRepositoryImpl(databaseDataSource = get())
    }
}
