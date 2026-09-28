package com.fstengineering.leasilycrm.occupiedPeriods.di

import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriods.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriods.presentation.OccupiedPeriodsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val occupiedPeriodsViewModelModule = module {
    viewModel {
        OccupiedPeriodsViewModel(
            registerStayCheckOutUseCase = get(),
            undoRegisterStayCheckOutUseCase = get(),
            occupiedPeriodSharedCommandRepository = get(),
            occupiedPeriodQueryRepository = get<OccupiedPeriodQueryRepository>(),
            occupiedPeriodSharedQueryRepository = get<OccupiedPeriodSharedQueryRepository>(),
        )
    }
}
