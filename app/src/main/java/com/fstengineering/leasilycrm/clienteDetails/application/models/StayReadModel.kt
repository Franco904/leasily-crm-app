package com.fstengineering.leasilycrm.clienteDetails.application.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime

data class StayReadModel(
    val id: Long,
    val checkInDateTime: AppDateTime,
    val isCheckOutDone: Boolean,
)
