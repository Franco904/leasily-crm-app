package com.fstengineering.leasilycrm.propertyRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.presentation.models.PropertyFileUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList

sealed interface PropertyDraftFormUiState {
    data object Loading : PropertyDraftFormUiState

    data class Loaded(
        val id: Long? = null,
        val createDateTime: AppDateTime? = null,
        val owner: ProprietarioUiState? = null,
        val type: PropertyType? = null,
        val neighborhood: String? = null,
        val streetAddress: String? = null,
        val cepPostalCode: String? = null,
        val number: Int? = null,
        val addressLine2: String? = null,
        val parkingSpotNumber: String? = null,
        val parkingBasementLevelNumber: String? = null,
        val hobbyBoxNumber: String? = null,
        val bedroomsCount: Int = 0,
        val enSuitesCount: Int = 0,
        val doubleBedsCount: Int = 0,
        val singleBedsCount: Int = 0,
        val bathroomsCount: Int = 0,
        val parkingSpotsCount: Int = 0,
        val airConditionersCount: Int = 0,
        val maxGuestsCount: Int = 0,
        val seaProximityTypeId: Long? = null,
        val distanceToSea: AppDecimal? = null,
        val includedItems: String? = null,
        val hasIncludedItems: Boolean = true,
        val characteristics: ImmutableList<PropertyCharacteristic> = persistentListOf(),
        val cleaningServiceTax: AppDecimal? = null,
        val hasCleaningServiceIncluded: Boolean = true,
        val prices: PropertyPricesUiState = PropertyPricesUiState(persistentMapOf()),
        val files: ImmutableList<PropertyFileUiState> = persistentListOf(),
        val observations: String? = null,
    ) : PropertyDraftFormUiState {
        val pricesByMonthYear get() = prices.pricesByMonthYear

        fun addFile(newFile: PropertyFileUiState) = copy(
            files = (files + newFile).toPersistentList(),
        )

        companion object {
            val Empty = Loaded()
        }
    }
}
