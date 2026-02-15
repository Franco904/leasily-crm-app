package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

data class AssistanceSearchedNeighborhood(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val neighborhood: String,
    val assistanceId: Long,
    val isDeleted: Boolean,
)
