package com.fstengineering.leasilycrm.propertyRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.presentation.models.PropertyFileUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

sealed interface PropertyDraftFormUiState {
    data object Loading : PropertyDraftFormUiState

    data class Loaded(
        val id: Long?,
        val createDateTime: AppDateTime?,
        val owner: PropertyOwnerUiState?,
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
        val prices: PropertyPricesUiState,
        val files: ImmutableList<PropertyFileUiState>,
        val observations: String?,
    ) : PropertyDraftFormUiState {
        val pricesByMonthYear get() = prices.pricesByMonthYear

        fun addFile(newFile: PropertyFileUiState) = copy(
            files = (files + newFile).toPersistentList(),
        )
    }
}
