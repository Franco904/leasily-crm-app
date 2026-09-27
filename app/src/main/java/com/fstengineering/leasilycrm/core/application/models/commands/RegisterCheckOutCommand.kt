package com.fstengineering.leasilycrm.core.application.models.commands

data class RegisterCheckOutCommand(
    val stayId: Long,
    val tenantId: Long?,
)
