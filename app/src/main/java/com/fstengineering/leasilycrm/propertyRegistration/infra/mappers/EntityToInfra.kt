package com.fstengineering.leasilycrm.propertyRegistration.infra.mappers

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.Property
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.PropertyFile
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.PropertyPrice

fun Property.toLocalDataModel(): PropertyEntity {
    return PropertyEntity(
        id = id,
        createDateTime = createDateTime ?: AppDateTime.now(),
        updateDateTime = AppDateTime.now(),
        ownerId = ownerId,
        typeId = type.id,
        neighborhood = neighborhood,
        streetAddress = streetAddress,
        cepPostalCode = cepPostalCode,
        number = number,
        addressLine2 = addressLine2,
        parkingSpotNumber = parkingSpotNumber,
        parkingBasementLevelNumber = parkingBasementLevelNumber,
        hobbyBoxNumber = hobbyBoxNumber,
        bedrooms = featuresCount.bedroomsCount,
        enSuites = featuresCount.enSuitesCount,
        doubleBeds = featuresCount.doubleBedsCount,
        singleBeds = featuresCount.singleBedsCount,
        bathrooms = featuresCount.bathroomsCount,
        parkingSpots = featuresCount.parkingSpotsCount,
        airConditioners = featuresCount.airConditionersCount,
        maxGuests = featuresCount.maxGuestsCount,
        seaProximityTypeId = seaProximityType?.id,
        distanceToSea = (seaProximityType as? PropertySeaProximityType.DistanceToSea)?.distance,
        includedItems = includedItems,
        hasIncludedItems = hasIncludedItems,
        cleaningServiceTax = cleaningServiceTax,
        hasCleaningServiceIncluded = hasCleaningServiceIncluded,
        observations = observations,
        isDeleted = false,
        contractVersion = BuildConfig.CONTRACT_VERSION,
    )
}

fun PropertyPrice.toLocalDataModel(propertyId: Long): PropertyPriceEntity {
    return PropertyPriceEntity(
        id = id,
        startDate = period.startDate,
        endDate = period.endDate,
        amount = amount.value,
        propertyId = propertyId,
    )
}

fun PropertyFile.toLocalDataModel(propertyId: Long): PropertyFileEntity {
    return PropertyFileEntity(
        id = id,
        propertyFileUri = uriString,
        isMainPicture = isMainPicture,
        propertyId = propertyId,
    )
}
