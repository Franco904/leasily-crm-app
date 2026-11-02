package com.fstengineering.leasilycrm.proprietarioDetails.application.models

import kotlinx.collections.immutable.ImmutableList

data class ProprietarioReadModel(
    val name: String?,
    val phone: String?,
    val email: String?,
    val paymentKey: String?,
    val files: ImmutableList<ProprietarioFileReadModel>,
)
