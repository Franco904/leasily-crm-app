package com.fstengineering.leasilycrm.imovelRegistration.presentation.mappers

import com.fstengineering.leasilycrm.core.presentation.models.ImovelFileUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelPriceUiState
import com.fstengineering.leasilycrm.imovelRegistration.application.models.commands.ImovelCommandModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.commands.ImovelFileCommandModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.commands.ImovelPriceCommandModel
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelDraftFormUiState
import kotlinx.collections.immutable.toPersistentList

fun ImovelDraftFormUiState.Loaded.toCommandModel(): ImovelCommandModel {
    return ImovelCommandModel(
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

fun ImovelPriceUiState.toCommandModel() = ImovelPriceCommandModel(
    id = id,
    startDate = startDate,
    endDate = endDate,
    amount = amount,
)

fun ImovelFileUiState.toCommandModel() = ImovelFileCommandModel(
    id = id,
    uriString = uri.toString(),
    isMainPicture = isMainPicture,
)
