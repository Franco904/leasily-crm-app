package com.fstengineering.leasilycrm.imovelDetails.presentation.models

data class ImovelAddressUiState(
    val neighborhood: String,
    val streetAddress: String,
    val cepPostalCode: String?,
    val number: Int?,
    val addressLine2: String?,
    val parkingSpotNumber: String?,
    val parkingBasementLevelNumber: String?,
    val hobbyBoxNumber: String?,
)
