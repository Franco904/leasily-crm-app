package com.fstengineering.leasilycrm.propertyRegistration.presentation.mappers

import com.fstengineering.leasilycrm.core.presentation.models.PropertyFileUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyPriceUiState
import com.fstengineering.leasilycrm.propertyRegistration.application.models.commands.PropertyCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.commands.PropertyFileCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.commands.PropertyPriceCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyDraftFormUiState
import kotlinx.collections.immutable.toPersistentList

fun PropertyDraftFormUiState.Loaded.toCommandModel(): PropertyCommandModel {
    return PropertyCommandModel(
        id = id,
        createDateTime = createDateTime,
        ownerId = owner?.id,
        type = type,
        neighborhood = neighborhood,
        streetAddress = streetAddress,
        cepPostalCode = cepPostalCode,
        number = number,
        addressLine2 = addressLine2,
        parkingSpotNumber = parkingSpotNumber,
        parkingBasementLevelNumber = parkingBasementLevelNumber,
        hobbyBoxNumber = hobbyBoxNumber,
        bedroomsCount = bedroomsCount,
        enSuitesCount = enSuitesCount,
        doubleBedsCount = doubleBedsCount,
        singleBedsCount = singleBedsCount,
        bathroomsCount = bathroomsCount,
        parkingSpotsCount = parkingSpotsCount,
        airConditionersCount = airConditionersCount,
        maxGuestsCount = maxGuestsCount,
        seaProximityTypeId = seaProximityTypeId,
        distanceToSea = distanceToSea,
        includedItems = includedItems,
        hasIncludedItems = hasIncludedItems,
        characteristics = characteristics,
        cleaningServiceTax = cleaningServiceTax,
        hasCleaningServiceIncluded = hasCleaningServiceIncluded,
        prices = pricesByMonthYear.values
            .flatten()
            .map { it.toCommandModel() }
            .toPersistentList(),
        files = files.map { it.toCommandModel() }.toPersistentList(),
        observations = observations,
    )
}

fun PropertyPriceUiState.toCommandModel() = PropertyPriceCommandModel(
    id = id,
    startDate = startDate,
    endDate = endDate,
    amount = amount,
)

fun PropertyFileUiState.toCommandModel() = PropertyFileCommandModel(
    id = id,
    uriString = uri.toString(),
    isMainPicture = isMainPicture,
)
