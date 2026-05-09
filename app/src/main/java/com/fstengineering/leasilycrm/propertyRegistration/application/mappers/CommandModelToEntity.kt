package com.fstengineering.leasilycrm.propertyRegistration.application.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.propertyRegistration.application.models.commands.PropertyCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.commands.PropertyFileCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.commands.PropertyPriceCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.Property
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.PropertyFile
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.PropertyPrice
import kotlinx.collections.immutable.toPersistentList

fun PropertyCommandModel.toEntity(): AppResultCompound<Property, DomainError> {
    val errors = mutableListOf<DomainError>()

    val prices = prices
        .mapNotNull { price ->
            price.toEntity()
                .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })
        }
        .toPersistentList()

    val files = files
        .map { file -> file.toEntity() }
        .toPersistentList()

    val parsed = Property.parse(
        id = id,
        createDateTime = createDateTime,
        ownerId = ownerId,
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
        .fold(onErrors = { errors.addAll(0, it); null }, onSuccess = { it })

    if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

    return AppResultCompound.Success(data = requireNotNull(parsed))
}

private fun PropertyPriceCommandModel.toEntity() = PropertyPrice.parse(
    id = id,
    startDate = startDate,
    endDate = endDate,
    amount = amount,
)

private fun PropertyFileCommandModel.toEntity() = PropertyFile(
    id = id,
    uriString = uriString,
    isMainPicture = isMainPicture,
)
