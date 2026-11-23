package com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

/** Intervalo de um Período Ocupado do Imóvel, do check-in ao check-out. */
data class OccupiedPeriodInterval(
    val stayId: Long,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
)
