package com.fstengineering.leasilycrm.occupiedPeriodDetails.di

import com.fstengineering.leasilycrm.core.di.qualifiers.registerStayCheckOutQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.undoRegisterStayCheckOutQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.watchOccupiedPeriodQualifier
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.OccupiedPeriodDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val occupiedPeriodDetailsViewModelModule = module {
    viewModel {
        OccupiedPeriodDetailsViewModel(
            watchOccupiedPeriod = get(watchOccupiedPeriodQualifier),
            registerStayCheckOut = get(registerStayCheckOutQualifier),
            undoRegisterStayCheckOut = get(undoRegisterStayCheckOutQualifier),
            occupiedPeriodSharedCommandRepository = get(),
            occupiedPeriodQueryRepository = get<OccupiedPeriodQueryRepository>(),
        )
    }
}
