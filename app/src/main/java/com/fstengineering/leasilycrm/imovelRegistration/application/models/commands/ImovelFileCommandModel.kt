package com.fstengineering.leasilycrm.imovelRegistration.application.models.commands

data class ImovelFileCommandModel(
    val id: Long?,
    val uriString: String,
    val isMainPicture: Boolean,
)
