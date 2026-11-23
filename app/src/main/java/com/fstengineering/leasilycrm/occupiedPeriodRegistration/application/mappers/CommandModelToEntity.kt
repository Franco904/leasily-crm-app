package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.toDomainErrors
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.StayCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.StayFileCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities.Stay
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities.StayFile
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities.UnavailablePeriod
import kotlinx.collections.immutable.toPersistentList

fun StayCommandModel.toEntity(): AppResult<Stay> {
    val fileErrors = mutableListOf<DomainError>()

    val files = files
        .mapNotNull { file ->
            file.toEntity()
                .fold(onError = { fileErrors.addAll(it.toDomainErrors()); null }, onSuccess = { it })
        }
        .toPersistentList()

    val stay = Stay.parse(
        id = id,
        createDateTime = createDateTime,
        imovelId = imovelId,
        tenantId = tenantId,
        vehicleMake = vehicleMake,
        vehicleColor = vehicleColor,
        vehicleLicensePlate = vehicleLicensePlate,
        guests = guests,
        checkInDateTime = checkInDateTime,
        checkInTime = checkInTime,
        checkOutDateTime = checkOutDateTime,
        checkOutTime = checkOutTime,
        isAutoCheckIn = isAutoCheckIn,
        isAutoCheckOut = isAutoCheckOut,
        totalPrice = totalPrice,
        commission = commission,
        files = files,
        observations = observations,
    )

    if (fileErrors.isEmpty()) return stay

    val errors = fileErrors + ((stay as? AppResult.Error)?.error?.toDomainErrors() ?: emptyList())
    return AppResult.Error(ValidationErrors(errors.toPersistentList()))
}

private fun StayFileCommandModel.toEntity() = StayFile.parse(
    id = id,
    uriString = uriString,
    name = name,
)

fun UnavailablePeriodCommandModel.toEntity() = UnavailablePeriod.parse(
    id = id,
    createDateTime = createDateTime,
    imovelId = imovelId,
    checkInDateTime = checkInDateTime,
    checkOutDateTime = checkOutDateTime,
    observations = observations,
)
