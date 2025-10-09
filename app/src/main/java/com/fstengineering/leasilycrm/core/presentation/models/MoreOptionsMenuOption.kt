package com.fstengineering.leasilycrm.core.presentation.models

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

data class MoreOptionsMenuOption(
    val icon: ImageVector? = null,
    @DrawableRes val iconDrawable: Int? = null,
    val text: String? = null,
    val onClick: () -> Unit = {},
    val isHorizontalDivider: Boolean = false,
)
