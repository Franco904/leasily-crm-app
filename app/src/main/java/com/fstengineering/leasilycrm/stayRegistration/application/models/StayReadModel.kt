package com.fstengineering.leasilycrm.stayRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import kotlinx.collections.immutable.ImmutableList

data class StayReadModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val property: PropertyReadModel?,
    val tenant: TenantReadModel?,
    val vehicleMake: String?,
    val vehicleColor: String?,
    val vehicleLicensePlate: String?,
    val guests: Int?,
    val checkInDateTime: AppDateTime?,
    val checkInTime: Pair<Int?, Int?>,
    val checkOutDateTime: AppDateTime?,
    val checkOutTime: Pair<Int?, Int?>,
    val isAutoCheckIn: Boolean?,
    val isAutoCheckOut: Boolean?,
    val totalPrice: AppDecimal?,
    val commission: AppDecimal?,
    val files: ImmutableList<StayFileReadModel>?,
    val observations: String?,
) : QueryUseCase.ReadModel {
    companion object {
        fun create(
            stayRaw: StayRawReadModel?,
            property: PropertyReadModel?,
            tenant: TenantReadModel?,
        ) = StayReadModel(
            id = stayRaw?.id,
            createDateTime = stayRaw?.createDateTime,
            property = property,
            tenant = tenant,
            vehicleMake = stayRaw?.vehicleMake,
            vehicleColor = stayRaw?.vehicleColor,
            vehicleLicensePlate = stayRaw?.vehicleLicensePlate,
            guests = stayRaw?.guests,
            checkInDateTime = stayRaw?.checkInDateTime,
            checkInTime = stayRaw?.checkInTime?.first to stayRaw?.checkInTime?.second,
            checkOutDateTime = stayRaw?.checkOutDateTime,
            checkOutTime = stayRaw?.checkOutTime?.first to stayRaw?.checkOutTime?.second,
            isAutoCheckIn = stayRaw?.isAutoCheckIn,
            isAutoCheckOut = stayRaw?.isAutoCheckOut,
            totalPrice = stayRaw?.totalPrice,
            commission = stayRaw?.commission,
            files = stayRaw?.files,
            observations = stayRaw?.observations,
        )
    }
}
