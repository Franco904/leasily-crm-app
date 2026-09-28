package com.fstengineering.leasilycrm.propertyOccupation.di

import com.fstengineering.leasilycrm.propertyOccupation.application.useCases.WatchPropertyOccupationUseCase
import org.koin.dsl.module

val propertyOccupationUseCaseModule = module {
    factory {
        WatchPropertyOccupationUseCase(
            occupiedPeriodQueryRepository = get(),
            leadTenantSharedQueryRepository = get(),
        )
    }
}
