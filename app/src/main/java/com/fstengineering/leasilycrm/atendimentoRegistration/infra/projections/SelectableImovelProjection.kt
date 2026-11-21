package com.fstengineering.leasilycrm.atendimentoRegistration.infra.projections

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal

data class SelectableImovelProjection(
    val id: Long,
    val typeId: Long,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val seaProximityTypeId: Long?,
    val distanceToSea: AppDecimal?,
    val bedrooms: Int,
    val mainPictureUriString: String?,
)
