package com.fstengineering.leasilycrm.imovelOcupacao.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class ImovelPeriodoOcupadoRawReadModel(
    val id: Long,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
    val tenantId: Long?,
) {
    val isUnavailablePeriod get() = tenantId != null
}
