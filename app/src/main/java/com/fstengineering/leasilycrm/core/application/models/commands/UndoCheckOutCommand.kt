package com.fstengineering.leasilycrm.core.application.models.commands

data class UndoCheckOutCommand(
    val stayId: Long,
    val tenantId: Long?,
)
