package com.fstengineering.leasilycrm.propertyDetails.models

import androidx.annotation.StringRes
import com.fstengineering.leasilycrm.R

enum class PropertyDetailsTab(
    val id: Int,
    @StringRes val labelId: Int,
) {
    Identification(1, R.string.property_details_tab_identification_label_title),
    Financial(2, R.string.property_details_top_financial_label_title);

    companion object {
        fun fromId(id: Int) = PropertyDetailsTab.entries.find { it.id == id }
    }
}
