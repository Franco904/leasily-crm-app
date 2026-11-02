package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class UnavailablePeriodReadModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val imovel: ImovelReadModel?,
    val checkInDateTime: AppDateTime?,
    val checkOutDateTime: AppDateTime?,
    val observations: String?,
) {
    companion object {
        fun create(
            unavailablePeriodRaw: UnavailablePeriodRawReadModel?,
            imovel: ImovelReadModel?,
        ) = UnavailablePeriodReadModel(
            id = unavailablePeriodRaw?.id,
            createDateTime = unavailablePeriodRaw?.createDateTime,
            imovel = imovel,
            checkInDateTime = unavailablePeriodRaw?.checkInDateTime,
            checkOutDateTime = unavailablePeriodRaw?.checkOutDateTime,
            observations = unavailablePeriodRaw?.observations,
        )
    }
}
