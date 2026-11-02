package com.fstengineering.leasilycrm.imovelOccupation.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class ImovelOccupiedPeriodRawReadModel(
    val id: Long,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
    val tenantId: Long?,
) {
    val isUnavailablePeriod get() = tenantId != null
}
