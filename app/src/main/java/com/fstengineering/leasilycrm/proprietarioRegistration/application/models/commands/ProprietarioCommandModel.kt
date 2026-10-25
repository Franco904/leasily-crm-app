package com.fstengineering.leasilycrm.proprietarioRegistration.application.models.commands

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import kotlinx.collections.immutable.ImmutableList

data class ProprietarioCommandModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val name: String?,
    val phone: String?,
    val email: String?,
    val paymentKey: String?,
    val files: ImmutableList<ProprietarioFileCommandModel>,
)
