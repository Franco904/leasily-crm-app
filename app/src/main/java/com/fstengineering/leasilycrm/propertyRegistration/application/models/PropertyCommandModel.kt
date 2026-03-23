package com.fstengineering.leasilycrm.propertyRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import kotlinx.collections.immutable.ImmutableList

data class PropertyCommandModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val ownerId: Long?,
    val type: PropertyType?,
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
    val characteristics: ImmutableList<PropertyCharacteristic>,
    val cleaningServiceTax: AppDecimal?,
    val hasCleaningServiceIncluded: Boolean,
    val prices: ImmutableList<PropertyPriceCommandModel>,
    val files: ImmutableList<PropertyFileCommandModel>,
    val observations: String?,
) : CommandUseCase.CommandModel
