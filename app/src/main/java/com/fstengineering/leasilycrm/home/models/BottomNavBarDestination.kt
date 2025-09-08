package com.fstengineering.leasilycrm.home.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.ui.graphics.vector.ImageVector
import com.fstengineering.leasilycrm.R

enum class BottomNavBarDestination(
    @StringRes val textId: Int,
    val icon: ImageVector? = null,
    val selectedIcon: ImageVector? = null,
    @DrawableRes val iconDrawable: Int? = null,
    @DrawableRes val selectedIconDrawable: Int? = null,
) {
    Dashboard(
        textId = R.string.home_dashboard_label,
        iconDrawable = R.drawable.team_dashboard,
        selectedIconDrawable = R.drawable.team_dashboard_filled,
    ),
    Leads(
        textId = R.string.home_leads_label,
        icon = Icons.Outlined.PersonSearch,
        selectedIcon = Icons.Default.PersonSearch,
    ),
    Tenants(
        textId = R.string.home_tenants_label,
        icon = Icons.Outlined.Group,
        selectedIcon = Icons.Default.Group,
    ),
    Stays(
        textId = R.string.home_stays_label,
        icon = Icons.Outlined.DateRange,
        selectedIcon = Icons.Default.DateRange,
    ),
}