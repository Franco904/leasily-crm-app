package com.fstengineering.leasilycrm.imovelRegistration.application.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.imovelRegistration.application.models.commands.ImovelCommandModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.commands.ImovelFileCommandModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.commands.ImovelPriceCommandModel
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.Imovel
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelFile
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelPrice
import kotlinx.collections.immutable.toPersistentList

fun ImovelCommandModel.toEntity(): AppResult<Imovel, ValidationErrors> {
    val errors = mutableListOf<DomainError>()

    val prices = prices
        .mapNotNull { price ->
            price.toEntity()
                .fold(onError = { errors.addAll(it.errors); null }, onSuccess = { it })
        }
        .toPersistentList()

    val files = files
        .map { file -> file.toEntity() }
        .toPersistentList()

    val parsed = Imovel.parse(
        id = id,
        createDateTime = createDateTime,
        proprietarioId = proprietarioId,
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
        files = files,
        observations = observations,
    )
        .fold(onError = { errors.addAll(0, it.errors); null }, onSuccess = { it })

    if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

    return AppResult.Success(data = requireNotNull(parsed))
}

private fun ImovelPriceCommandModel.toEntity() = ImovelPrice.parse(
    id = id,
    startDate = startDate,
    endDate = endDate,
    amount = amount,
)

private fun ImovelFileCommandModel.toEntity() = ImovelFile(
    id = id,
    uriString = uriString,
    isMainPicture = isMainPicture,
)
