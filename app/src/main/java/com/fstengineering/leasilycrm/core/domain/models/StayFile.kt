package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDateTime

data class StayFile(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val uriString: String? = null,
    val name: String? = null,
    val stayId: Long? = null,
    val isDeleted: Boolean = false,
)
