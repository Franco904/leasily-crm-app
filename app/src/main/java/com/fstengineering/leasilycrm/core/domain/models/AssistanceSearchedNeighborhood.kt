package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

data class AssistanceSearchedNeighborhood(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val neighborhood: String? = null,
    val assistanceId: Long? = null,
    val isDeleted: Boolean = false,
)
