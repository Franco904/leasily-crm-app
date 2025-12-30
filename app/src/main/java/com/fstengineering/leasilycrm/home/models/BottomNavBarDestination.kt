package com.fstengineering.leasilycrm.home.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.ui.graphics.vector.ImageVector
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.R

enum class BottomNavBarDestination(
    val textId: Int,
    val icon: ImageVector? = null,
    val selectedIcon: ImageVector? = null,
    val iconDrawable: Int? = null,
    val selectedIconDrawable: Int? = null,
    val navDestination: Any,
) {
    Dashboard(
        textId = R.string.home_dashboard_label,
        iconDrawable = R.drawable.team_dashboard,
        selectedIconDrawable = R.drawable.team_dashboard_filled,
        navDestination = Destinations.Home.Dashboard,
    ),
    Leads(
        textId = R.string.home_leads_label,
        icon = Icons.Outlined.PersonSearch,
        selectedIcon = Icons.Default.PersonSearch,
        navDestination = Destinations.Home.Leads,
    ),
    Tenants(
        textId = R.string.home_tenants_label,
        icon = Icons.Outlined.Group,
        selectedIcon = Icons.Default.Group,
        navDestination = Destinations.Home.Tenants,
    ),
    Stays(
        textId = R.string.home_stays_label,
        icon = Icons.Outlined.DateRange,
        selectedIcon = Icons.Default.DateRange,
        navDestination = Destinations.Home.Stays,
    ),
}