package com.fstengineering.leasilycrm.core.presentation.models

import android.net.Uri

data class PropertyFileUiState(
    val id: Long? = null,
    val uri: Uri,
    val isMainPicture: Boolean,
)
