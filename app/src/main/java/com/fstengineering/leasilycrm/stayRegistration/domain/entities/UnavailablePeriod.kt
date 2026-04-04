package com.fstengineering.leasilycrm.stayRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.stayRegistration.domain.valueObjects.StayPeriod
import kotlinx.collections.immutable.toPersistentList

class UnavailablePeriod private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val propertyId: Long,
    val period: StayPeriod,
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
        ): AppResultCompound<UnavailablePeriod, DomainError> {
            val errors = mutableListOf<DomainError>()

            if (propertyId == null) errors.add(DomainError.StayProperty.Required)

            val period = when {
                checkInDateTime == null || checkOutDateTime == null -> {
                    errors.add(DomainError.StayPeriod.Required)
                    null
                }

                else -> {
                    StayPeriod.parse(
                        checkInDateTime = checkInDateTime.withHourMinute(12, 0),
                        checkOutDateTime = checkOutDateTime.withHourMinute(12, 0),
                    )
                }
            }
                ?.fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = UnavailablePeriod(
                id = id,
                createDateTime = createDateTime,
                propertyId = requireNotNull(propertyId),
                period = requireNotNull(period),
                observations = observations,
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
