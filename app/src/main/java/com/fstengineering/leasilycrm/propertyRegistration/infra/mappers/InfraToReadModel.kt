package com.fstengineering.leasilycrm.propertyRegistration.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyFileReadModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyPriceReadModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyRawReadModel
import kotlinx.collections.immutable.toPersistentList

fun PropertyCompound.toRawReadModel(): PropertyRawReadModel {
    val characteristics = characteristics
        .mapNotNull { PropertyCharacteristic.fromId(it.propertyCharacteristicId) }
        .toPersistentList()

    val prices = prices
        .map { it.toReadModel() }
        .toPersistentList()

    val files = files
        .map { it.toReadModel() }
        .toPersistentList()

    return PropertyRawReadModel(
        id = property.id,
        createDateTime = property.createDateTime,
        ownerId = property.ownerId,
        type = requireNotNull(PropertyType.fromId(property.typeId)),
        neighborhood = property.neighborhood,
        streetAddress = property.streetAddress,
        cepPostalCode = property.cepPostalCode,
        number = property.number,
        addressLine2 = property.addressLine2,
        parkingSpotNumber = property.parkingSpotNumber,
        parkingBasementLevelNumber = property.parkingBasementLevelNumber,
        hobbyBoxNumber = property.hobbyBoxNumber,
        bedroomsCount = property.bedrooms,
        enSuitesCount = property.enSuites,
        doubleBedsCount = property.doubleBeds,
        singleBedsCount = property.singleBeds,
        bathroomsCount = property.bathrooms,
        parkingSpotsCount = property.parkingSpots,
        airConditionersCount = property.airConditioners,
        maxGuestsCount = property.maxGuests,
        seaProximityTypeId = property.seaProximityTypeId,
        distanceToSea = property.distanceToSea,
        includedItems = property.includedItems,
        hasIncludedItems = property.hasIncludedItems,
        characteristics = characteristics,
        cleaningServiceTax = property.cleaningServiceTax,
        hasCleaningServiceIncluded = property.hasCleaningServiceIncluded,
        prices = prices,
        files = files,
        observations = property.observations,
    )
}

fun ProprietarioOrm.toProprietarioReadModel() = ProprietarioReadModel(
    id = requireNotNull(id),
    name = name,
)

private fun PropertyPriceEntity.toReadModel() = PropertyPriceReadModel(
    id = id,
    startDate = startDate,
    endDate = endDate,
    amount = amount,
)

private fun PropertyFileEntity.toReadModel() = PropertyFileReadModel(
    id = id,
    uriString = propertyFileUri,
    isMainPicture = isMainPicture,
)
