package com.fstengineering.leasilycrm.core.presentation.models

data class ClienteUiState(
    val id: Long,
    val name: String,
    val city: String?,
    val hospedesCount: Int? = null,
    val hasUnfinishedAssistance: Boolean = false,
    val hasUnfinishedStay: Boolean = false,
)
