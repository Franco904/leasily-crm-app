package com.fstengineering.leasilycrm.imovelRegistration.infra.mappers

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelSeaProximityType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelPriceOrm
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.Imovel
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelFile
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelPrice

fun Imovel.toLocalDataModel(): ImovelOrm {
    return ImovelOrm(
        id = id,
        createDateTime = createDateTime ?: AppDateTime.now(),
        updateDateTime = AppDateTime.now(),
        ownerId = ownerId,
        typeId = type.id,
        neighborhood = neighborhood,
        streetAddress = streetAddress,
        cepPostalCode = cepPostalCode,
        number = number,
        addressLine2 = addressLine2,
        parkingSpotNumber = parkingSpotNumber,
        parkingBasementLevelNumber = parkingBasementLevelNumber,
        hobbyBoxNumber = hobbyBoxNumber,
        bedrooms = bedroomsCount.value,
        enSuites = enSuitesCount.value,
        doubleBeds = doubleBedsCount.value,
        singleBeds = singleBedsCount.value,
        bathrooms = bathroomsCount.value,
        parkingSpots = parkingSpotsCount.value,
        airConditioners = airConditionersCount.value,
        maxGuests = maxGuestsCount.value,
        seaProximityTypeId = seaProximityType?.id,
        distanceToSea = (seaProximityType as? ImovelSeaProximityType.DistanceToSea)?.distance,
        includedItems = includedItems,
        hasIncludedItems = hasIncludedItems,
        cleaningServiceTax = cleaningServiceTax,
        hasCleaningServiceIncluded = hasCleaningServiceIncluded,
        observations = observations,
        isDeleted = false,
        contractVersion = BuildConfig.CONTRACT_VERSION,
    )
}

fun ImovelPrice.toLocalDataModel(imovelId: Long): ImovelPriceOrm {
    return ImovelPriceOrm(
        id = id,
        startDate = period.startDate,
        endDate = period.endDate,
        amount = amount.value,
        imovelId = imovelId,
    )
}

fun ImovelFile.toLocalDataModel(imovelId: Long): ImovelFileOrm {
    return ImovelFileOrm(
        id = id,
        imovelFileUri = uriString,
        isMainPicture = isMainPicture,
        imovelId = imovelId,
    )
}
