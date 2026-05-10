package com.fstengineering.leasilycrm.occupiedPeriodDetails.di

import com.fstengineering.leasilycrm.core.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories.OccupiedPeriodQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories.PropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories.TenantQueryRepositoryImpl
import org.koin.dsl.module

val occupiedPeriodDetailsRepositoryModule = module {
    single<OccupiedPeriodQueryRepository> {
        OccupiedPeriodQueryRepositoryImpl(
            stayDatabaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<PropertyQueryRepository> {
        PropertyQueryRepositoryImpl(
            propertyDatabaseDataSource = get(),
        )
    }

    single<TenantQueryRepository> {
        TenantQueryRepositoryImpl(
            tenantDatabaseDataSource = get(),
        )
    }
}
