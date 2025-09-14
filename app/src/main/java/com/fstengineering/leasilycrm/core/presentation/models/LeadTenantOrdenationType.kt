package com.fstengineering.leasilycrm.core.presentation.models

import androidx.annotation.StringRes
import com.fstengineering.leasilycrm.R

enum class LeadTenantOrdenationType(
    val id: Int,
    @StringRes val label: Int?,
) {
    Name(1, R.string.leads_order_by_name_text),
    LastUpdateDate(2, R.string.leads_order_by_last_update_date_text);

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: Name
    }
}