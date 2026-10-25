package com.fstengineering.leasilycrm.proprietarioRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import kotlinx.collections.immutable.ImmutableList

data class ProprietarioReadModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val name: String?,
    val phone: String?,
    val email: String?,
    val paymentKey: String?,
    val files: ImmutableList<ProprietarioFileReadModel>,
)
