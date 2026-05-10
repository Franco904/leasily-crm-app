package com.fstengineering.leasilycrm.occupiedPeriodDetails.di

import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.OccupiedPeriodDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val occupiedPeriodDetailsViewModelModule = module {
    viewModel {
        OccupiedPeriodDetailsViewModel(
            watchOccupiedPeriod = get(),
            registerStayCheckOut = get(),
            undoRegisterStayCheckOut = get(),
            occupiedPeriodSharedCommandRepository = get(),
            occupiedPeriodQueryRepository = get<OccupiedPeriodQueryRepository>(),
        )
    }
}
