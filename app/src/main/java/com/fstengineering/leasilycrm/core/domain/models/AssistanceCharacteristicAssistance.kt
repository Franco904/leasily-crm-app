package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

data class AssistanceCharacteristicAssistance(
    val assistanceId: Long? = null,
    val assistanceCharacteristic: AssistanceCharacteristic? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val isDeleted: Boolean = false,
)
