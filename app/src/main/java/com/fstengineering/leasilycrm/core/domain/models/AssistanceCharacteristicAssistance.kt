package com.fstengineering.leasilycrm.core.domain.models

import java.time.LocalDateTime

data class AssistanceCharacteristicAssistance(
    val assistanceId: Long? = null,
    val assistanceCharacteristic: AssistanceCharacteristic? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val isDeleted: Boolean = false,
)
