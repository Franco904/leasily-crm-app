package com.fstengineering.leasilycrm.stayRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import kotlinx.collections.immutable.ImmutableList

data class StayCommandModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val propertyId: Long?,
    val tenantId: Long?,
    val vehicleMake: String?,
    val vehicleColor: String?,
    val vehicleLicensePlate: String?,
    val guests: Int,
    val checkInDateTime: AppDateTime?,
    val checkInTime: Pair<Int?, Int?>,
    val checkOutDateTime: AppDateTime?,
    val checkOutTime: Pair<Int?, Int?>,
    val isAutoCheckIn: Boolean,
    val isAutoCheckOut: Boolean,
    val totalPrice: AppDecimal?,
    val commission: AppDecimal?,
    val files: ImmutableList<StayFileCommandModel>,
    val observations: String?,
) : CommandUseCase.CommandModel
