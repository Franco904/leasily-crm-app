package com.fstengineering.leasilycrm.core.domain.models

import java.time.LocalDateTime

data class AssistanceSearchedNeighborhood(
    val id: Long? = null,
    val createDateTime: LocalDateTime? = null,
    val updateDateTime: LocalDateTime? = null,
    val neighborhood: String? = null,
    val assistanceId: Long? = null,
    val isDeleted: Boolean = false,
)
