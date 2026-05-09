package com.fstengineering.leasilycrm.propertyDetails.presentation.models

import com.fstengineering.leasilycrm.R

enum class PropertyDetailsTab(
    val id: Int,
    val labelId: Int,
) {
    Identification(1, R.string.property_details_tab_identification_label_title),
    Financial(2, R.string.property_details_top_financial_label_title);

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: Identification
    }
}
