package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

data class AssistanceCharacteristicAssistance(
    val assistanceId: Long,
    val assistanceCharacteristic: AssistanceCharacteristic,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val isDeleted: Boolean,
)
