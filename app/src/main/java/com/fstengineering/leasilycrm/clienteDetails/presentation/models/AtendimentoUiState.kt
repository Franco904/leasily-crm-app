package com.fstengineering.leasilycrm.clienteDetails.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

data class AtendimentoUiState(
    val id: Long,
    val updateDateTime: AppDateTime,
    val searchingStartDate: AppDate?,
    val searchingEndDate: AppDate?,
    val hasFlexibilityForPeriod: Boolean,
    val maxAcceptedNightlyRate: AppDecimal?,
    val qtAdultHospedes: Int,
    val qtKidHospedes: Int,
    val qtBabyHospedes: Int,
    val qtAnimalHospedes: Int,
    val qtBedrooms: Int,
    val qtEnSuites: Int,
    val qtDoubleBeds: Int,
    val qtSingleBeds: Int,
    val qtBathrooms: Int,
    val qtParkingSpots: Int,
    val preferredSeaProximityTypeId: Long?,
    val distanceToSea: AppDecimal?,
    val preferredNeighborhoods: ImmutableList<String>,
    val preferredImovelCharacteristics: ImmutableList<ImovelCharacteristic>,
    val observations: String?,
    val lastSentImovel: ImovelCardUiState?,
    val sentImoveis: ImmutableList<ImovelCardUiState>,
    val lastInteracao: InteracaoUiState?,
    val interacoesByMonth: ImmutableMap<AppYearMonth, ImmutableList<InteracaoUiState>>,
    val isFinished: Boolean,
)
