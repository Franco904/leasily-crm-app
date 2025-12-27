package com.fstengineering.leasilycrm.core.presentation.models

import androidx.compose.ui.graphics.vector.ImageVector

data class MoreOptionsMenuOption(
    val icon: ImageVector? = null,
    val iconDrawable: Int? = null,
    val text: String? = null,
    val onClick: () -> Unit = {},
    val isHorizontalDivider: Boolean = false,
)
