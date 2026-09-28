package com.fstengineering.leasilycrm.propertyDashboard.di

import com.fstengineering.leasilycrm.propertyDashboard.application.useCases.WatchPropertyUseCase
import org.koin.dsl.module

val propertyDashboardUseCaseModule = module {
    factory {
        WatchPropertyUseCase(
            propertyQueryRepository = get(),
            propertyOwnerQueryRepository = get(),
            stayQueryRepository = get(),
            leadTenantSharedQueryRepository = get(),
            dateProvider = get(),
        )
    }
}
