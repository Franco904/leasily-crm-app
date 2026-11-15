package com.fstengineering.leasilycrm.imovelDetails.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.presentation.models.ImovelFileUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelPriceUiState
import com.fstengineering.leasilycrm.core.presentation.models.PriceMonthYearUiState
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelFileReadModel
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelPriceReadModel
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelDetails.presentation.models.ImovelAddressUiState
import com.fstengineering.leasilycrm.imovelDetails.presentation.models.ImovelQuantityCharacteristicsUiState
import com.fstengineering.leasilycrm.imovelDetails.presentation.models.ImovelUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

fun ImovelReadModel.toUiState() = ImovelUiState.Loaded(
    id = id,
    type = type,
    address = ImovelAddressUiState(
        neighborhood = neighborhood,
        streetAddress = streetAddress,
        cepPostalCode = cepPostalCode,
        number = number,
        addressLine2 = addressLine2,
        parkingSpotNumber = parkingSpotNumber,
        parkingBasementLevelNumber = parkingBasementLevelNumber,
        hobbyBoxNumber = hobbyBoxNumber,
    ),
    quantityCharacteristics = ImovelQuantityCharacteristicsUiState(
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

private fun ImmutableList<ImovelPriceReadModel>.toUiState(): ImmutableMap<PriceMonthYearUiState, ImmutableList<ImovelPriceUiState>> {
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

private fun ImovelPriceReadModel.toUiState() = ImovelPriceUiState(
    startDate = startDate,
    endDate = endDate,
    amount = amount,
)

private fun ImovelFileReadModel.toUiState() = ImovelFileUiState(
    uri = uriString.toUri(),
    isMainPicture = isMainPicture,
)
