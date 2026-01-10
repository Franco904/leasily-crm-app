package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDateTime

data class CustomerSegment(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val name: String? = null,
    val isDeleted: Boolean = false,
    val type: CustomerSegmentType? = null,
    val userId: Long? = null,
)
