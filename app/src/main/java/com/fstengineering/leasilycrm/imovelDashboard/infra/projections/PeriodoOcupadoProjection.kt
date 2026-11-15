package com.fstengineering.leasilycrm.imovelDashboard.infra.projections

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class PeriodoOcupadoProjection(
    val id: Long,
    val tenantId: Long?,
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
)
