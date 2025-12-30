package com.fstengineering.leasilycrm.leadTenantDetails.models

import com.fstengineering.leasilycrm.R

enum class LeadTenantDetailsTab(
    val id: Int,
    val labelId: Int,
) {
    Details(1, R.string.lead_tenant_details_tab_details_label_title),
    CurrentAssistance(2, R.string.lead_tenant_details_top_current_assistance_label_title),
    History(3, R.string.lead_tenant_details_top_history_label_title);

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id }
    }
}