package com.fstengineering.leasilycrm.imovelRegistration.application.models.commands

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import kotlinx.collections.immutable.ImmutableList

data class ImovelCommandModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val ownerId: Long?,
    val type: ImovelType?,
    val neighborhood: String?,
    val streetAddress: String?,
    val cepPostalCode: String?,
    val number: Int?,
    val addressLine2: String?,
    val parkingSpotNumber: String?,
    val parkingBasementLevelNumber: String?,
    val hobbyBoxNumber: String?,
    val bedroomsCount: Int,
    val enSuitesCount: Int,
    val doubleBedsCount: Int,
    val singleBedsCount: Int,
    val bathroomsCount: Int,
    val parkingSpotsCount: Int,
    val airConditionersCount: Int,
    val maxGuestsCount: Int,
    val seaProximityTypeId: Long?,
    val distanceToSea: AppDecimal?,
    val includedItems: String?,
    val hasIncludedItems: Boolean,
    val characteristics: ImmutableList<ImovelCharacteristic>,
    val cleaningServiceTax: AppDecimal?,
    val hasCleaningServiceIncluded: Boolean,
    val prices: ImmutableList<ImovelPriceCommandModel>,
    val files: ImmutableList<ImovelFileCommandModel>,
    val observations: String?,
)
