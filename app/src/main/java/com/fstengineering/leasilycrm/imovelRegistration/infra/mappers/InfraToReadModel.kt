package com.fstengineering.leasilycrm.imovelRegistration.infra.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelPriceOrm
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ImovelFileReadModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ImovelPriceReadModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelRegistration.infra.projections.ImovelRegistrationProjection
import com.fstengineering.leasilycrm.imovelRegistration.infra.projections.ProprietarioNameProjection
import kotlinx.collections.immutable.toPersistentList

fun ImovelRegistrationProjection.toImovelReadModel(): ImovelReadModel {
    val proprietario = requireNotNull(proprietario?.takeUnless { it.isDeleted })

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
        createDateTime = imovel.createDateTime,
        proprietario = ProprietarioReadModel(id = proprietario.id, name = proprietario.name),
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
        maxHospedesCount = imovel.maxHospedes,
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

fun ProprietarioNameProjection.toProprietarioReadModel() = ProprietarioReadModel(
    id = id,
    name = name,
)

private fun ImovelPriceOrm.toReadModel() = ImovelPriceReadModel(
    id = id,
    startDate = startDate,
    endDate = endDate,
    amount = amount,
)

private fun ImovelFileOrm.toReadModel() = ImovelFileReadModel(
    id = id,
    uriString = imovelFileUri,
    isMainPicture = isMainPicture,
)
