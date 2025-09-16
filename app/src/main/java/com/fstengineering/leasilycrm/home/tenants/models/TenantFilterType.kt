package com.fstengineering.leasilycrm.home.tenants.models

import androidx.annotation.StringRes
import com.fstengineering.leasilycrm.R

enum class TenantFilterType(
    val id: Int,
    @StringRes val label: Int,
) {
    UpcomingStay(1, R.string.tenants_upcoming_stays_filter_text),
    Old(2, R.string.tenants_old_filter_text);

    companion object {
        fun fromId(id: Int) = TenantFilterType.entries.find { it.id == id } ?: UpcomingStay
    }
}
