package com.fstengineering.leasilycrm.imovelRegistration.presentation.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.presentation.models.ImovelFileUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelPriceUiState
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.Imovel
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelFile
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelPrice
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelDraftFormUiState
import kotlinx.collections.immutable.toPersistentList

fun ImovelDraftFormUiState.Loaded.toImovel(): AppResult<Imovel, ValidationErrors> {
    val errors = mutableListOf<DomainError>()

    val prices = pricesByMonthYear.values
        .flatten()
        .mapNotNull { price ->
            price.toImovelPrice()
                .fold(onError = { errors.addAll(it.errors); null }, onSuccess = { it })
        }
        .toPersistentList()

    val imovel = Imovel.parse(
        id = id,
        createDateTime = createDateTime,
        proprietarioId = proprietario?.id,
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
        prices = prices,
        files = files.map { it.toImovelFile() }.toPersistentList(),
        observations = observations,
    )
        .fold(onError = { errors.addAll(0, it.errors); null }, onSuccess = { it })

    if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

    return AppResult.Success(data = requireNotNull(imovel))
}

private fun ImovelPriceUiState.toImovelPrice() = ImovelPrice.parse(
    id = id,
    startDate = startDate,
    endDate = endDate,
    amount = amount,
)

private fun ImovelFileUiState.toImovelFile() = ImovelFile(
    id = id,
    uriString = uri.toString(),
    isMainPicture = isMainPicture,
)
