package com.fstengineering.leasilycrm.stayRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.Amount
import com.fstengineering.leasilycrm.stayRegistration.domain.valueObjects.StayGuestsCount
import com.fstengineering.leasilycrm.stayRegistration.domain.valueObjects.StayPeriod
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class Stay private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val propertyId: Long,
    val period: StayPeriod,
    val observations: String?,
    val tenantId: Long,
    val vehicleMake: String?,
    val vehicleColor: String?,
    val vehicleLicensePlate: String?,
    val guestsCount: StayGuestsCount,
    val isAutoCheckIn: Boolean,
    val isAutoCheckOut: Boolean,
    val totalPrice: Amount?,
    val commission: Amount,
    val files: ImmutableList<StayFile>,
) {
    fun putTenant(tenantId: Long): Stay = copyWith(tenantId = tenantId)

    private fun copyWith(
        id: Long? = this.id,
        createDateTime: AppDateTime? = this.createDateTime,
        propertyId: Long = this.propertyId,
        tenantId: Long = this.tenantId,
        vehicleMake: String? = this.vehicleMake,
        vehicleColor: String? = this.vehicleColor,
        vehicleLicensePlate: String? = this.vehicleLicensePlate,
        guestsCount: StayGuestsCount = this.guestsCount,
        period: StayPeriod = this.period,
        isAutoCheckIn: Boolean = this.isAutoCheckIn,
        isAutoCheckOut: Boolean = this.isAutoCheckOut,
        totalPrice: Amount? = this.totalPrice,
        commission: Amount = this.commission,
        files: ImmutableList<StayFile> = this.files,
        observations: String? = this.observations,
    ) = Stay(
        id = id,
        createDateTime = createDateTime,
        propertyId = propertyId,
        tenantId = tenantId,
        vehicleMake = vehicleMake,
        vehicleColor = vehicleColor,
        vehicleLicensePlate = vehicleLicensePlate,
        guestsCount = guestsCount,
        period = period,
        isAutoCheckIn = isAutoCheckIn,
        isAutoCheckOut = isAutoCheckOut,
        totalPrice = totalPrice,
        commission = commission,
        files = files,
        observations = observations,
    )

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
        ): AppResultCompound<Stay, DomainError> {
            val errors = mutableListOf<DomainError>()

            if (propertyId == null) errors.add(DomainError.StayProperty.Required)
            if (tenantId == null) errors.add(DomainError.StayTenant.Required)

            val guestsCount = StayGuestsCount.parse(guests)
                .fold(onError = { errors.add(it); null }, onSuccess = { it })

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
                    StayPeriod.parse(
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
                ?.fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })

            val totalPriceParsed = totalPrice?.let { Amount.parse(totalPrice) }
                ?.fold(onError = { errors.add(it); null }, onSuccess = { it })

            if (commission == null) errors.add(DomainError.StayCommission.Required)
            val commissionParsed = commission?.let { Amount.parse(commission) }
                ?.fold(onError = { errors.add(it); null }, onSuccess = { it })

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = Stay(
                id = id,
                createDateTime = createDateTime,
                propertyId = requireNotNull(propertyId),
                tenantId = requireNotNull(tenantId),
                vehicleMake = vehicleMake,
                vehicleColor = vehicleColor,
                vehicleLicensePlate = vehicleLicensePlate,
                guestsCount = requireNotNull(guestsCount),
                period = requireNotNull(period),
                isAutoCheckIn = isAutoCheckIn,
                isAutoCheckOut = isAutoCheckOut,
                totalPrice = totalPriceParsed,
                commission = requireNotNull(commissionParsed),
                files = files,
                observations = observations,
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
