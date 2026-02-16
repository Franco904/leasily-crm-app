package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime

data class AssistanceSentProperty(
    val property: Property,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val isDeleted: Boolean,
)
