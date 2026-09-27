package com.fstengineering.leasilycrm.occupiedPeriodDetails.di

import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.useCases.WatchOccupiedPeriodUseCase
import org.koin.dsl.module

val occupiedPeriodDetailsUseCaseModule = module {
    factory {
        WatchOccupiedPeriodUseCase(
            occupiedPeriodQueryRepository = get(),
            propertyQueryRepository = get(),
            tenantQueryRepository = get(),
        )
    }
}
