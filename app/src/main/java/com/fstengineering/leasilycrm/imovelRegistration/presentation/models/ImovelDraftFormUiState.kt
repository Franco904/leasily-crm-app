package com.fstengineering.leasilycrm.imovelRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.presentation.models.ImovelFileUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList

sealed interface ImovelDraftFormUiState {
    data object Loading : ImovelDraftFormUiState

    data class Loaded(
        val id: Long? = null,
        val createDateTime: AppDateTime? = null,
        val proprietario: ProprietarioUiState? = null,
        val type: ImovelType? = null,
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
        val characteristics: ImmutableList<ImovelCharacteristic> = persistentListOf(),
        val cleaningServiceTax: AppDecimal? = null,
        val hasCleaningServiceIncluded: Boolean = true,
        val prices: ImovelPricesUiState = ImovelPricesUiState(persistentMapOf()),
        val files: ImmutableList<ImovelFileUiState> = persistentListOf(),
        val observations: String? = null,
    ) : ImovelDraftFormUiState {
        val pricesByMonthYear get() = prices.pricesByMonthYear

        fun addFile(newFile: ImovelFileUiState) = copy(
            files = (files + newFile).toPersistentList(),
        )

        companion object {
            val Empty = Loaded()
        }
    }
}
