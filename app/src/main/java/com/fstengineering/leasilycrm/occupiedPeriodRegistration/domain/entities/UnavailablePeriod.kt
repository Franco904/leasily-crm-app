package com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.valueObjects.OccupiedPeriodPeriod
import kotlinx.collections.immutable.toPersistentList

class UnavailablePeriod private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val propertyId: Long,
    val period: OccupiedPeriodPeriod,
    val observations: String?,
) {
    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            propertyId: Long?,
            checkInDateTime: AppDateTime?,
            checkOutDateTime: AppDateTime?,
            observations: String?,
        ): AppResult<UnavailablePeriod, ValidationErrors> {
            val errors = mutableListOf<DomainError>()

            if (propertyId == null) errors.add(DomainError.StayProperty.Required)

            val period = when {
                checkInDateTime == null || checkOutDateTime == null -> {
                    errors.add(DomainError.StayPeriod.Required)
                    null
                }

                else -> {
                    OccupiedPeriodPeriod.parse(
                        checkInDateTime = checkInDateTime.withHourMinute(12, 0),
                        checkOutDateTime = checkOutDateTime.withHourMinute(12, 0),
                    )
                }
            }
                ?.fold(onError = { errors.addAll(it.errors); null }, onSuccess = { it })

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = UnavailablePeriod(
                id = id,
                createDateTime = createDateTime,
                propertyId = requireNotNull(propertyId),
                period = requireNotNull(period),
                observations = observations,
            )

            return AppResult.Success(data = parsed)
        }
    }
}
