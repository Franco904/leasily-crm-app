package com.fstengineering.leasilycrm.imovelRegistration.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.presentation.models.ImovelFileUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelPriceUiState
import com.fstengineering.leasilycrm.core.presentation.models.PriceMonthYearUiState
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ImovelFileReadModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ImovelPriceReadModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelDraftFormUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelPricesUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ProprietarioUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

fun ImovelReadModel.toUiState() = ImovelDraftFormUiState.Loaded(
    id = id,
    createDateTime = createDateTime,
    proprietario = proprietario?.toUiState(),
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
    maxHospedesCount = maxHospedesCount,
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

private fun ImmutableList<ImovelPriceReadModel>.toUiState(): ImovelPricesUiState {
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

    return ImovelPricesUiState(pricesByMonthYear)
}

private fun ImovelPriceReadModel.toUiState() = ImovelPriceUiState(
    id = id,
    startDate = startDate,
    endDate = endDate,
    amount = amount,
)

private fun ImovelFileReadModel.toUiState() = ImovelFileUiState(
    id = id,
    uri = uriString.toUri(),
    isMainPicture = isMainPicture,
)

fun ProprietarioReadModel.toUiState() = ProprietarioUiState(
    id = id,
    name = name,
)
