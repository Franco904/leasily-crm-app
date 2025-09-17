package com.fstengineering.leasilycrm.core.domain.models

import java.time.LocalDate

data class AssistanceInteraction(
    val id: Long? = null,
    val interactionDate: LocalDate? = null,
    val title: String? = null,
    val description: String? = null,
    val assistanceId: Long? = null,
    val isDeleted: Boolean = false,
)
