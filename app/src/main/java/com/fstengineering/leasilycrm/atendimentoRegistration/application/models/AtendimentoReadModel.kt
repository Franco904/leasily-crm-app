package com.fstengineering.leasilycrm.atendimentoRegistration.application.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import kotlinx.collections.immutable.ImmutableList

data class AtendimentoReadModel(
    val id: Long,
    val clienteId: Long,
    val clienteName: String,
    val createDateTime: AppDateTime,
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
    val preferredSeaProximityTypeId: Long?,
    val preferredDistanceToSea: AppDecimal?,
    val preferredNeighborhoods: ImmutableList<String>,
    val preferredImovelCharacteristics: ImmutableList<ImovelCharacteristic>,
    val observations: String?,
    val imoveisEnviados: ImmutableList<ImovelEnviadoReadModel>,
    val interacoes: ImmutableList<InteracaoReadModel>,
)
