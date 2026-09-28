package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class UnavailablePeriodReadModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val property: PropertyReadModel?,
    val checkInDateTime: AppDateTime?,
    val checkOutDateTime: AppDateTime?,
    val observations: String?,
) {
    companion object {
        fun create(
            unavailablePeriodRaw: UnavailablePeriodRawReadModel?,
            property: PropertyReadModel?,
        ) = UnavailablePeriodReadModel(
            id = unavailablePeriodRaw?.id,
            createDateTime = unavailablePeriodRaw?.createDateTime,
            property = property,
            checkInDateTime = unavailablePeriodRaw?.checkInDateTime,
            checkOutDateTime = unavailablePeriodRaw?.checkOutDateTime,
            observations = unavailablePeriodRaw?.observations,
        )
    }
}
