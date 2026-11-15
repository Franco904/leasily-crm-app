package com.fstengineering.leasilycrm.core.domain.valueObjects

data class ImovelPeriodoOcupado(
    val id: Long,
    val tenantId: Long?,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
) {
    val isUnavailablePeriod get() = tenantId == null
}
