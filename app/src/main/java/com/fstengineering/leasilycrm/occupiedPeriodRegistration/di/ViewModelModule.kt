package com.fstengineering.leasilycrm.occupiedPeriodRegistration.di

import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.OccupiedPeriodRegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val occupiedPeriodRegistrationViewModelModule = module {
    viewModel {
        OccupiedPeriodRegistrationViewModel(
            readStay = get(),
            readUnavailablePeriod = get(),
            readSelectableProperties = get(),
            writeStay = get(),
            writeUnavailablePeriod = get(),
            propertyQueryRepository = get(),
            tenantQueryRepository = get(),
            fileStorageService = get(),
        )
    }
}
