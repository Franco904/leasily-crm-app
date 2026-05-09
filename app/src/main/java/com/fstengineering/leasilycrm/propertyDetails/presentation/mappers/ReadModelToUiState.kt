package com.fstengineering.leasilycrm.propertyDetails.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.presentation.models.PriceMonthYearUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyFileUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyPriceUiState
import com.fstengineering.leasilycrm.propertyDetails.application.models.dtos.PropertyFileReadModel
import com.fstengineering.leasilycrm.propertyDetails.application.models.dtos.PropertyPriceReadModel
import com.fstengineering.leasilycrm.propertyDetails.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.propertyDetails.presentation.models.PropertyAddressUiState
import com.fstengineering.leasilycrm.propertyDetails.presentation.models.PropertyQuantityCharacteristicsUiState
import com.fstengineering.leasilycrm.propertyDetails.presentation.models.PropertyUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

fun PropertyReadModel.toUiState() = PropertyUiState.Loaded(
    id = id,
    type = type,
    address = PropertyAddressUiState(
        neighborhood = neighborhood,
        streetAddress = streetAddress,
        cepPostalCode = cepPostalCode,
        number = number,
        addressLine2 = addressLine2,
        parkingSpotNumber = parkingSpotNumber,
        parkingBasementLevelNumber = parkingBasementLevelNumber,
        hobbyBoxNumber = hobbyBoxNumber,
    ),
    quantityCharacteristics = PropertyQuantityCharacteristicsUiState(
        bedroomsCount = bedroomsCount,
        enSuitesCount = enSuitesCount,
        doubleBedsCount = doubleBedsCount,
        singleBedsCount = singleBedsCount,
        bathroomsCount = bathroomsCount,
        parkingSpotsCount = parkingSpotsCount,
        airConditionersCount = airConditionersCount,
        maxGuestsCount = maxGuestsCount,
    ),
    seaProximityTypeId = seaProximityTypeId,
    distanceToSea = distanceToSea,
    includedItems = includedItems,
    hasIncludedItems = hasIncludedItems,
    characteristics = characteristics,
    cleaningServiceTax = cleaningServiceTax,
    hasCleaningServiceIncluded = hasCleaningServiceIncluded,
    prices = prices.toUiState(),
    files = files
        .map { it.toUiState() }
        .toPersistentList(),
    observations = observations,
)

private fun ImmutableList<PropertyPriceReadModel>.toUiState(): ImmutableMap<PriceMonthYearUiState, ImmutableList<PropertyPriceUiState>> {
    val pricesByMonthYear = this
        .sortedBy { it.startDate }
        .groupBy {
            PriceMonthYearUiState(
                month = it.startDate.month,
                year = it.startDate.year,
            )
        }
        .mapValues { (_, prices) ->
            prices
                .map { it.toUiState() }
                .toPersistentList()
        }
        .toPersistentMap()

    return pricesByMonthYear
}

private fun PropertyPriceReadModel.toUiState() = PropertyPriceUiState(
    startDate = startDate,
    endDate = endDate,
    amount = amount,
)

private fun PropertyFileReadModel.toUiState() = PropertyFileUiState(
    uri = uriString.toUri(),
    isMainPicture = isMainPicture,
)
