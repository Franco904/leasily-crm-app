package com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import kotlinx.collections.immutable.ImmutableList

data class ImovelReadModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val proprietario: ProprietarioReadModel?,
    val type: ImovelType?,
    val neighborhood: String?,
    val streetAddress: String?,
    val cepPostalCode: String?,
    val number: Int?,
    val addressLine2: String?,
    val parkingSpotNumber: String?,
    val parkingBasementLevelNumber: String?,
    val hobbyBoxNumber: String?,
    val bedroomsCount: Int?,
    val enSuitesCount: Int?,
    val doubleBedsCount: Int?,
    val singleBedsCount: Int?,
    val bathroomsCount: Int?,
    val parkingSpotsCount: Int?,
    val airConditionersCount: Int?,
    val maxGuestsCount: Int?,
    val seaProximityTypeId: Long?,
    val distanceToSea: AppDecimal?,
    val includedItems: String?,
    val hasIncludedItems: Boolean?,
    val characteristics: ImmutableList<ImovelCharacteristic>?,
    val cleaningServiceTax: AppDecimal?,
    val hasCleaningServiceIncluded: Boolean?,
    val prices: ImmutableList<ImovelPriceReadModel>?,
    val files: ImmutableList<ImovelFileReadModel>?,
    val observations: String?,
) {
    companion object {
        fun create(
            imovelRaw: ImovelRawReadModel?,
            proprietario: ProprietarioReadModel?,
        ) = ImovelReadModel(
            id = imovelRaw?.id,
            createDateTime = imovelRaw?.createDateTime,
            proprietario = proprietario,
            type = imovelRaw?.type,
            neighborhood = imovelRaw?.neighborhood,
            streetAddress = imovelRaw?.streetAddress,
            cepPostalCode = imovelRaw?.cepPostalCode,
            number = imovelRaw?.number,
            addressLine2 = imovelRaw?.addressLine2,
            parkingSpotNumber = imovelRaw?.parkingSpotNumber,
            parkingBasementLevelNumber = imovelRaw?.parkingBasementLevelNumber,
            hobbyBoxNumber = imovelRaw?.hobbyBoxNumber,
            bedroomsCount = imovelRaw?.bedroomsCount,
            enSuitesCount = imovelRaw?.enSuitesCount,
            doubleBedsCount = imovelRaw?.doubleBedsCount,
            singleBedsCount = imovelRaw?.singleBedsCount,
            bathroomsCount = imovelRaw?.bathroomsCount,
            parkingSpotsCount = imovelRaw?.parkingSpotsCount,
            airConditionersCount = imovelRaw?.airConditionersCount,
            maxGuestsCount = imovelRaw?.maxGuestsCount,
            seaProximityTypeId = imovelRaw?.seaProximityTypeId,
            distanceToSea = imovelRaw?.distanceToSea,
            includedItems = imovelRaw?.includedItems,
            hasIncludedItems = imovelRaw?.hasIncludedItems,
            characteristics = imovelRaw?.characteristics,
            cleaningServiceTax = imovelRaw?.cleaningServiceTax,
            hasCleaningServiceIncluded = imovelRaw?.hasCleaningServiceIncluded,
            prices = imovelRaw?.prices,
            files = imovelRaw?.files,
            observations = imovelRaw?.observations,
        )
    }
}
