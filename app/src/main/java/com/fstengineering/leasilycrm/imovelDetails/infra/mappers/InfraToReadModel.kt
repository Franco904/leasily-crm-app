package com.fstengineering.leasilycrm.imovelDetails.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelPriceOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelFileReadModel
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelPriceReadModel
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelReadModel
import kotlinx.collections.immutable.toPersistentList

fun ImovelCompound.toRawReadModel(): ImovelReadModel {
    val characteristics = characteristics
        .mapNotNull { ImovelCharacteristic.fromId(it.imovelCharacteristicId) }
        .toPersistentList()

    val prices = prices
        .map { it.toReadModel() }
        .toPersistentList()

    val files = files
        .map { it.toReadModel() }
        .toPersistentList()

    return ImovelReadModel(
        id = requireNotNull(imovel.id),
        type = requireNotNull(ImovelType.fromId(imovel.typeId)),
        neighborhood = imovel.neighborhood,
        streetAddress = imovel.streetAddress,
        cepPostalCode = imovel.cepPostalCode,
        number = imovel.number,
        addressLine2 = imovel.addressLine2,
        parkingSpotNumber = imovel.parkingSpotNumber,
        parkingBasementLevelNumber = imovel.parkingBasementLevelNumber,
        hobbyBoxNumber = imovel.hobbyBoxNumber,
        bedroomsCount = imovel.bedrooms,
        enSuitesCount = imovel.enSuites,
        doubleBedsCount = imovel.doubleBeds,
        singleBedsCount = imovel.singleBeds,
        bathroomsCount = imovel.bathrooms,
        parkingSpotsCount = imovel.parkingSpots,
        airConditionersCount = imovel.airConditioners,
        maxGuestsCount = imovel.maxGuests,
        seaProximityTypeId = imovel.seaProximityTypeId,
        distanceToSea = imovel.distanceToSea,
        includedItems = imovel.includedItems,
        hasIncludedItems = imovel.hasIncludedItems,
        characteristics = characteristics,
        cleaningServiceTax = imovel.cleaningServiceTax,
        hasCleaningServiceIncluded = imovel.hasCleaningServiceIncluded,
        prices = prices,
        files = files,
        observations = imovel.observations,
    )
}

private fun ImovelPriceOrm.toReadModel() = ImovelPriceReadModel(
    startDate = startDate,
    endDate = endDate,
    amount = amount,
)

private fun ImovelFileOrm.toReadModel() = ImovelFileReadModel(
    uriString = imovelFileUri,
    isMainPicture = isMainPicture,
)
