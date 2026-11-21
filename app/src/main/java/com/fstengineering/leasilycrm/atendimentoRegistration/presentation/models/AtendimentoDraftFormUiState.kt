package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface AtendimentoDraftFormUiState {
    data object Loading : AtendimentoDraftFormUiState

    data class Loaded(
        val id: Long?,
        val createDateTime: AppDateTime?,
        val preferredStartDate: AppDate?,
        val preferredEndDate: AppDate?,
        val hasFlexibilityForPeriod: Boolean,
        val maxAcceptedNightlyRate: AppDecimal?,
        val adultsCount: Int,
        val kidsCount: Int,
        val babiesCount: Int,
        val animalsCount: Int,
        val bedroomsCount: Int,
        val enSuitesCount: Int,
        val doubleBedsCount: Int,
        val singleBedsCount: Int,
        val bathroomsCount: Int,
        val parkingSpotsCount: Int,
        val preferredNeighborhoods: ImmutableList<String>,
        val preferredSeaProximityTypeId: Long?,
        val preferredDistanceToSea: AppDecimal?,
        val preferredImovelCharacteristics: ImmutableList<ImovelCharacteristic>,
        val observations: String?,
        val imoveisEnviados: ImmutableList<ImovelCardUiState>,
        val interacoes: ImmutableList<InteracaoDraftFormUiState.Loaded>,
        val clienteId: Long,
        val clienteName: String,
    ) : AtendimentoDraftFormUiState {
        val needValidation = preferredSeaProximityTypeId != null

        companion object {
            fun empty(clienteId: Long, clienteName: String) = Loaded(
                id = null,
                createDateTime = null,
                preferredStartDate = null,
                preferredEndDate = null,
                hasFlexibilityForPeriod = false,
                maxAcceptedNightlyRate = null,
                adultsCount = 0,
                kidsCount = 0,
                babiesCount = 0,
                animalsCount = 0,
                bedroomsCount = 0,
                enSuitesCount = 0,
                doubleBedsCount = 0,
                singleBedsCount = 0,
                bathroomsCount = 0,
                parkingSpotsCount = 0,
                preferredNeighborhoods = persistentListOf(),
                preferredSeaProximityTypeId = null,
                preferredDistanceToSea = null,
                preferredImovelCharacteristics = persistentListOf(),
                observations = null,
                imoveisEnviados = persistentListOf(),
                interacoes = persistentListOf(),
                clienteId = clienteId,
                clienteName = clienteName,
            )
        }
    }
}
