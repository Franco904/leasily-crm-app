package com.fstengineering.leasilycrm.core.domain.models

import java.time.LocalDate
import java.time.LocalDateTime

data class AssistanceInteraction(
    val id: Long? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val interactionDate: LocalDate? = null,
    val title: String? = null,
    val description: String? = null,
    val assistanceId: Long? = null,
    val isDeleted: Boolean = false,
)
