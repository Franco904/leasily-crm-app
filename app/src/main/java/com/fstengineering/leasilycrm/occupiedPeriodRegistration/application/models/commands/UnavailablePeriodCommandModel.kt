package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class UnavailablePeriodCommandModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val propertyId: Long?,
    val checkInDateTime: AppDateTime?,
    val checkOutDateTime: AppDateTime?,
    val observations: String?,
) : CommandUseCase.CommandModel
