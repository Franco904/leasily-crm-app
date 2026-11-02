package com.fstengineering.leasilycrm.imovelOccupation.di

import com.fstengineering.leasilycrm.imovelOccupation.application.useCases.WatchImovelOccupationUseCase
import org.koin.dsl.module

val imovelOccupationUseCaseModule = module {
    factory {
        WatchImovelOccupationUseCase(
            occupiedPeriodQueryRepository = get(),
            leadTenantSharedQueryRepository = get(),
        )
    }
}
