package com.fstengineering.leasilycrm.propertyRegistration.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.presentation.models.PropertyFileUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyPriceUiState
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyFileReadModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyOwnerReadModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyPriceReadModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PriceMonthYearUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyDraftFormUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyOwnerUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyPricesUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

fun PropertyReadModel.toUiState() = PropertyDraftFormUiState.Loaded(
    id = id,
    createDateTime = createDateTime,
    owner = owner?.toUiState(),
    type = type,
    neighborhood = neighborhood,
    streetAddress = streetAddress,
    cepPostalCode = cepPostalCode,
    number = number,
    addressLine2 = addressLine2,
    parkingSpotNumber = parkingSpotNumber,
    parkingBasementLevelNumber = parkingBasementLevelNumber,
    hobbyBoxNumber = hobbyBoxNumber,
    bedroomsCount = bedroomsCount ?: 0,
    enSuitesCount = enSuitesCount ?: 0,
    doubleBedsCount = doubleBedsCount ?: 0,
    singleBedsCount = singleBedsCount ?: 0,
    bathroomsCount = bathroomsCount ?: 0,
    parkingSpotsCount = parkingSpotsCount ?: 0,
    airConditionersCount = airConditionersCount ?: 0,
    maxGuestsCount = maxGuestsCount ?: 0,
    seaProximityTypeId = seaProximityTypeId,
    distanceToSea = distanceToSea,
    includedItems = includedItems,
    hasIncludedItems = hasIncludedItems == true,
    characteristics = characteristics ?: persistentListOf(),
    cleaningServiceTax = cleaningServiceTax,
    hasCleaningServiceIncluded = hasCleaningServiceIncluded == true,
    prices = (prices ?: persistentListOf()).toUiState(),
    files = files
        ?.map { it.toUiState() }
        ?.toPersistentList() ?: persistentListOf(),
    observations = observations,
)

fun ImmutableList<PropertyPriceReadModel>.toUiState(): PropertyPricesUiState {
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

    return PropertyPricesUiState(pricesByMonthYear)
}

fun PropertyPriceReadModel.toUiState() = PropertyPriceUiState(
    id = id,
    startDate = startDate,
    endDate = endDate,
    amount = amount,
)

fun PropertyFileReadModel.toUiState() = PropertyFileUiState(
    id = id,
    uri = uriString.toUri(),
    isMainPicture = isMainPicture,
)

fun PropertyOwnerReadModel.toUiState() = PropertyOwnerUiState(
    id = id,
    name = name,
)
