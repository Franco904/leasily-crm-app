package com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.Amount
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.Quantidade
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.valueObjects.OccupiedPeriodPeriod
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class Stay private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val propertyId: Long,
    val period: OccupiedPeriodPeriod,
    val observations: String?,
    val tenantId: Long,
    val vehicleMake: String?,
    val vehicleColor: String?,
    val vehicleLicensePlate: String?,
    val guestsCount: Quantidade,
    val isAutoCheckIn: Boolean,
    val isAutoCheckOut: Boolean,
    val totalPrice: Amount?,
    val commission: Amount,
    val files: ImmutableList<StayFile>,
) {
    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            propertyId: Long?,
            tenantId: Long?,
            vehicleMake: String?,
            vehicleColor: String?,
            vehicleLicensePlate: String?,
            guests: Int,
            checkInDateTime: AppDateTime?,
            checkInTime: Pair<Int?, Int?>,
            checkOutDateTime: AppDateTime?,
            checkOutTime: Pair<Int?, Int?>,
            isAutoCheckIn: Boolean,
            isAutoCheckOut: Boolean,
            totalPrice: AppDecimal?,
            commission: AppDecimal?,
            files: ImmutableList<StayFile>,
            observations: String?,
        ): AppResult<Stay, ValidationErrors> {
            val errors = mutableListOf<DomainError>()

            if (propertyId == null) errors.add(DomainError.StayProperty.Required)
            if (tenantId == null) errors.add(DomainError.StayTenant.Required)

            val (checkInTimeHour, checkInTimeMinute) = checkInTime
            val (checkOutTimeHour, checkOutTimeMinute) = checkOutTime

            val period = when {
                checkInDateTime == null || checkOutDateTime == null -> {
                    errors.add(DomainError.StayPeriod.Required)
                    null
                }

                checkInTimeHour == null || checkInTimeMinute == null -> {
                    errors.add(DomainError.StayCheckInTime.Required)
                    null
                }

                checkOutTimeHour == null || checkOutTimeMinute == null -> {
                    errors.add(DomainError.StayCheckOutTime.Required)
                    null
                }

                else -> {
                    OccupiedPeriodPeriod.parse(
                        checkInDateTime = checkInDateTime.withHourMinute(
                            hour = checkInTimeHour,
                            minute = checkInTimeMinute,
                        ),
                        checkOutDateTime = checkOutDateTime.withHourMinute(
                            hour = checkOutTimeHour,
                            minute = checkOutTimeMinute,
                        ),
                    )
                }
            }
                ?.fold(onError = { errors.addAll(it.errors); null }, onSuccess = { it })

            if (commission == null) errors.add(DomainError.StayCommission.Required)

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = Stay(
                id = id,
                createDateTime = createDateTime,
                propertyId = requireNotNull(propertyId),
                tenantId = requireNotNull(tenantId),
                vehicleMake = vehicleMake,
                vehicleColor = vehicleColor,
                vehicleLicensePlate = vehicleLicensePlate,
                guestsCount = Quantidade.of(guests),
                period = requireNotNull(period),
                isAutoCheckIn = isAutoCheckIn,
                isAutoCheckOut = isAutoCheckOut,
                totalPrice = totalPrice?.let { Amount.of(it) },
                commission = Amount.of(requireNotNull(commission)),
                files = files,
                observations = observations,
            )

            return AppResult.Success(data = parsed)
        }
    }
}
