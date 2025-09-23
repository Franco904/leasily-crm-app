package com.fstengineering.leasilycrm.core.domain.models

import java.time.LocalDateTime

data class AssistanceSentProperty(
    val assistanceId: Long? = null,
    val property: Property? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val isDeleted: Boolean = false,
)
