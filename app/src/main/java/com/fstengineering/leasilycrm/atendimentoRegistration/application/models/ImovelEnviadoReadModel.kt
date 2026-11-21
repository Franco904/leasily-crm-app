package com.fstengineering.leasilycrm.atendimentoRegistration.application.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType

data class ImovelEnviadoReadModel(
    val id: Long,
    val type: ImovelType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val seaProximityTypeId: Long?,
    val distanceToSea: String?,
    val bedroomsCount: Int,
    val mainPictureUriString: String?,
)
