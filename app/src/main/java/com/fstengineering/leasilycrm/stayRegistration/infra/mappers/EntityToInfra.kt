package com.fstengineering.leasilycrm.stayRegistration.infra.mappers

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.stayRegistration.domain.entities.Stay
import com.fstengineering.leasilycrm.stayRegistration.domain.entities.StayFile
import com.fstengineering.leasilycrm.stayRegistration.domain.entities.UnavailablePeriod

fun Stay.toLocalDataModel(): StayEntity {
    return StayEntity(
        id = id,
        createDateTime = createDateTime ?: AppDateTime.now(),
        updateDateTime = AppDateTime.now(),
        vehicleMake = vehicleMake,
        vehicleColor = vehicleColor,
        vehicleLicensePlate = vehicleLicensePlate,
        guests = guestsCount.count,
        checkInDateTime = period.checkInDateTime,
        checkOutDateTime = period.checkOutDateTime,
        isAutoCheckIn = isAutoCheckIn,
        isAutoCheckOut = isAutoCheckOut,
        totalPrice = totalPrice?.value,
        commission = commission.value,
        propertyId = propertyId,
        tenantId = tenantId,
        observations = observations,
        isDeleted = false,
        contractVersion = BuildConfig.CONTRACT_VERSION,
    )
}

fun UnavailablePeriod.toLocalDataModel(): StayEntity {
    return StayEntity(
        id = id,
        createDateTime = createDateTime ?: AppDateTime.now(),
        updateDateTime = AppDateTime.now(),
        vehicleMake = null,
        vehicleColor = null,
        vehicleLicensePlate = null,
        guests = null,
        checkInDateTime = period.checkInDateTime,
        checkOutDateTime = period.checkOutDateTime,
        isAutoCheckIn = null,
        isAutoCheckOut = null,
        totalPrice = null,
        commission = null,
        propertyId = propertyId,
        tenantId = null,
        observations = observations,
        isDeleted = false,
        contractVersion = BuildConfig.CONTRACT_VERSION,
    )
}

fun StayFile.toLocalDataModel(stayId: Long) = StayFileEntity(
    id = id,
    stayFileUri = uriString,
    stayFileName = name,
    stayId = stayId,
)
