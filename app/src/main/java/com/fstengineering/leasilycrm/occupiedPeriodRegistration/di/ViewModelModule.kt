package com.fstengineering.leasilycrm.occupiedPeriodRegistration.di

import com.fstengineering.leasilycrm.core.di.qualifiers.readSelectablePropertiesQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.readStayQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.readUnavailablePeriodQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.writeStayQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.writeUnavailablePeriodQualifier
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.OccupiedPeriodRegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val occupiedPeriodRegistrationViewModelModule = module {
    viewModel {
        OccupiedPeriodRegistrationViewModel(
            readStay = get(readStayQualifier),
            readUnavailablePeriod = get(readUnavailablePeriodQualifier),
            readSelectableProperties = get(readSelectablePropertiesQualifier),
            writeStay = get(writeStayQualifier),
            writeUnavailablePeriod = get(writeUnavailablePeriodQualifier),
            propertyQueryRepository = get(),
            tenantQueryRepository = get(),
            fileStorageService = get(),
        )
    }
}
