package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class UnavailablePeriodCommandModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val imovelId: Long?,
    val checkInDateTime: AppDateTime?,
    val checkOutDateTime: AppDateTime?,
    val observations: String?,
)
