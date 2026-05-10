package com.fstengineering.leasilycrm.propertyDashboard.di

import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.infra.repositories.PropertyOwnerQueryRepositoryImpl
import com.fstengineering.leasilycrm.propertyDashboard.infra.repositories.PropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.propertyDashboard.infra.repositories.StayQueryRepositoryImpl
import org.koin.dsl.module

val propertyDashboardRepositoryModule = module {
    single<StayQueryRepository> {
        StayQueryRepositoryImpl(databaseDataSource = get())
    }

    single<PropertyOwnerQueryRepository> {
        PropertyOwnerQueryRepositoryImpl(databaseDataSource = get())
    }

    single<PropertyQueryRepository> {
        PropertyQueryRepositoryImpl(databaseDataSource = get())
    }
}
