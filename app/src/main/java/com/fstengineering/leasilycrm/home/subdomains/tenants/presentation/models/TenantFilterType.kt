package com.fstengineering.leasilycrm.home.subdomains.tenants.presentation.models

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus

enum class TenantFilterType(
    val status: TenantStatus,
    val label: Int,
) {
    UpcomingStay(TenantStatus.UpcomingStay, R.string.tenants_upcoming_stays_filter_text),
    Old(TenantStatus.Old, R.string.tenants_old_filter_text),
}
