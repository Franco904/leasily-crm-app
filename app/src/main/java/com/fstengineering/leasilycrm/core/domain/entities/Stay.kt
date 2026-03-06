package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayGuestsCount
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayPeriod
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import kotlinx.collections.immutable.ImmutableList

sealed class Stay(
    open val id: Long?,
    open val createDateTime: AppDateTime?,
    open val updateDateTime: AppDateTime?,
    open val propertyId: Long,
    open val period: StayPeriod,
    open val observations: String?,
    open val inconsistencies: ImmutableList<DomainError>,
    open val contractVersion: Int,
) {
    class Owned private constructor(
        override val id: Long?,
        override val createDateTime: AppDateTime?,
        override val updateDateTime: AppDateTime?,
        override val propertyId: Long,
        override val period: StayPeriod,
        override val observations: String?,
        override val inconsistencies: ImmutableList<DomainError>,
        override val contractVersion: Int,
        val tenantId: Long,
        val vehicleMake: String?,
        val vehicleColor: String?,
        val vehicleLicensePlate: String?,
        val guestsCount: StayGuestsCount,
        val isAutoCheckIn: Boolean,
        val isAutoCheckOut: Boolean,
        val totalPrice: AppDecimal?,
        val commission: AppDecimal,
        val status: StayStatus,
        val transfers: ImmutableList<StayTransfer>,
        val files: ImmutableList<StayFile>,
    ) : Stay(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        propertyId = propertyId,
        period = period,
        observations = observations,
        inconsistencies = inconsistencies,
        contractVersion = contractVersion,
    ) {
        fun registerCheckIn() = copyWith(status = StayStatus.CheckedIn)

        fun registerCheckOut() = copyWith(status = StayStatus.CheckedOut)

        fun undoRegisterCheckOut() = copyWith(status = StayStatus.CheckedIn)

        fun putTenant(tenantId: Long): Owned {
            validateTenant(tenantId).onError { throw it.asException() }
            return copyWith(tenantId = tenantId)
        }

        private fun copyWith(
            id: Long? = this.id,
            createDateTime: AppDateTime? = this.createDateTime,
            updateDateTime: AppDateTime? = this.updateDateTime,
            propertyId: Long = this.propertyId,
            tenantId: Long = this.tenantId,
            vehicleMake: String? = this.vehicleMake,
            vehicleColor: String? = this.vehicleColor,
            vehicleLicensePlate: String? = this.vehicleLicensePlate,
            guestsCount: StayGuestsCount = this.guestsCount,
            period: StayPeriod = this.period,
            isAutoCheckIn: Boolean = this.isAutoCheckIn,
            isAutoCheckOut: Boolean = this.isAutoCheckOut,
            status: StayStatus = this.status,
            totalPrice: AppDecimal? = this.totalPrice,
            commission: AppDecimal = this.commission,
            transfers: ImmutableList<StayTransfer> = this.transfers,
            files: ImmutableList<StayFile> = this.files,
            observations: String? = this.observations,
            inconsistencies: ImmutableList<DomainError> = this.inconsistencies,
            contractVersion: Int = this.contractVersion,
        ) = Owned(
            id = id,
            createDateTime = createDateTime,
            updateDateTime = updateDateTime,
            propertyId = propertyId,
            tenantId = tenantId,
            vehicleMake = vehicleMake,
            vehicleColor = vehicleColor,
            vehicleLicensePlate = vehicleLicensePlate,
            guestsCount = guestsCount,
            period = period,
            isAutoCheckIn = isAutoCheckIn,
            isAutoCheckOut = isAutoCheckOut,
            status = status,
            totalPrice = totalPrice,
            commission = commission,
            transfers = transfers,
            files = files,
            observations = observations,
            inconsistencies = inconsistencies,
            contractVersion = contractVersion,
        )

        companion object {
            fun parse(
                id: Long?,
                createDateTime: AppDateTime?,
                updateDateTime: AppDateTime?,
                propertyId: Long,
                tenantId: Long,
                vehicleMake: String?,
                vehicleColor: String?,
                vehicleLicensePlate: String?,
                guests: Int?,
                checkInDateTime: AppDateTime,
                checkInTime: Pair<Int?, Int?>,
                checkOutDateTime: AppDateTime,
                checkOutTime: Pair<Int?, Int?>,
                isAutoCheckIn: Boolean?,
                isAutoCheckOut: Boolean?,
                totalPrice: AppDecimal?,
                commission: AppDecimal?,
                transfers: ImmutableList<StayTransfer>,
                files: ImmutableList<StayFile>,
                observations: String?,
                statusId: Int?,
                inconsistencies: ImmutableList<DomainError>,
                contractVersion: Int,
            ): Owned {
                validateProperty(propertyId).onError { throw it.asException() }
                validateTenant(tenantId).onError { throw it.asException() }

                val guestsCount = StayGuestsCount.parse(guests)

                val period = StayPeriod.parse(
                    checkInDateTime = checkInDateTime,
                    checkInTime = checkInTime,
                    checkOutDateTime = checkOutDateTime,
                    checkOutTime = checkOutTime,
                )

                isAutoCheckIn ?: throw DomainError.Stay.IsAutoCheckInRequired.asException()
                isAutoCheckOut ?: throw DomainError.Stay.IsAutoCheckOutRequired.asException()
                statusId ?: throw DomainError.Stay.StatusRequired.asException()

                if (totalPrice != null) {
                    validateTotalPrice(totalPrice)
                        .onError { throw it.asException() }
                }

                validateCommission(commission)
                    .onError { throw it.asException() }

                val status = StayStatus.parse(statusId)

                return Owned(
                    id = id,
                    createDateTime = createDateTime,
                    updateDateTime = updateDateTime,
                    propertyId = propertyId,
                    tenantId = tenantId,
                    vehicleMake = vehicleMake,
                    vehicleColor = vehicleColor,
                    vehicleLicensePlate = vehicleLicensePlate,
                    guestsCount = guestsCount,
                    period = period,
                    isAutoCheckIn = isAutoCheckIn,
                    isAutoCheckOut = isAutoCheckOut,
                    status = status,
                    totalPrice = totalPrice,
                    commission = requireNotNull(commission),
                    transfers = transfers,
                    files = files,
                    observations = observations,
                    inconsistencies = inconsistencies,
                    contractVersion = contractVersion,
                )
            }

            fun validateTenant(tenantId: Long?): AppResult<Unit, DomainError.Stay> {
                return when {
                    tenantId == null -> AppResult.Error(DomainError.Stay.PropertyRequired)
                    else -> AppResult.Success(Unit)
                }
            }

            fun validateTotalPrice(totalPrice: AppDecimal): AppResult<Unit, DomainError.Stay> {
                return when {
                    totalPrice.isNegative -> AppResult.Error(DomainError.Stay.TotalPriceIsNegative)
                    else -> AppResult.Success(Unit)
                }
            }

            fun validateCommission(commission: AppDecimal?): AppResult<Unit, DomainError.Stay> {
                return when {
                    commission == null -> AppResult.Error(DomainError.Stay.CommissionRequired)
                    commission.isNegative -> AppResult.Error(DomainError.Stay.CommissionIsNegative)
                    else -> AppResult.Success(Unit)
                }
            }
        }
    }

    class UnavailablePeriod private constructor(
        override val id: Long?,
        override val createDateTime: AppDateTime?,
        override val updateDateTime: AppDateTime?,
        override val propertyId: Long,
        override val period: StayPeriod,
        override val observations: String?,
        override val inconsistencies: ImmutableList<DomainError>,
        override val contractVersion: Int,
    ) : Stay(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        propertyId = propertyId,
        period = period,
        observations = observations,
        inconsistencies = inconsistencies,
        contractVersion = contractVersion,
    ) {
        companion object {
            fun parse(
                id: Long?,
                createDateTime: AppDateTime?,
                updateDateTime: AppDateTime?,
                propertyId: Long,
                checkInDateTime: AppDateTime,
                checkOutDateTime: AppDateTime,
                observations: String?,
                inconsistencies: ImmutableList<DomainError>,
                contractVersion: Int,
            ): UnavailablePeriod {
                validateProperty(propertyId).onError { throw it.asException() }

                val period = StayPeriod.parse(
                    checkInDateTime = checkInDateTime,
                    checkInTime = 12 to 0,
                    checkOutDateTime = checkOutDateTime,
                    checkOutTime = 12 to 0,
                )

                return UnavailablePeriod(
                    id = id,
                    createDateTime = createDateTime,
                    updateDateTime = updateDateTime,
                    propertyId = propertyId,
                    period = period,
                    observations = observations,
                    inconsistencies = inconsistencies,
                    contractVersion = contractVersion,
                )
            }
        }
    }

    companion object {
        fun validateProperty(propertyId: Long?): AppResult<Unit, DomainError.Stay> {
            return when {
                propertyId == null -> AppResult.Error(DomainError.Stay.PropertyRequired)
                else -> AppResult.Success(Unit)
            }
        }
    }
}

//val isAnyFormFieldInitialized = property != null
//        || tenant != null
//        || vehicleMake != null
//        || vehicleColor != null
//        || vehicleLicensePlate != null
//        || guests != null
//        || checkInDateTime != null || checkInHourMinute != null
//        || checkOutDateTime != null || checkOutHourMinute != null
//        || isAutoCheckIn || isAutoCheckOut
//        || totalPrice != null || commission != null
//        || transfers.isNotEmpty()
//        || files.isNotEmpty()
//        || observations != null
