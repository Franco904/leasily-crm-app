package com.fstengineering.leasilycrm.core.presentation.models

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType.Companion.FAR_FROM_SEA_ID
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType.Companion.SEA_ACCESS_ID
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType.Companion.SEA_VIEW_ID

enum class PropertySeaProximityTypeDetails(
    val id: Long,
    val label: Int,
) {
    SeaAccess(SEA_ACCESS_ID, R.string.sea_access_property_sea_proximity_type_text),
    SeaView(SEA_VIEW_ID, R.string.sea_view_property_sea_proximity_type_text),
    FarFromSea(FAR_FROM_SEA_ID, R.string.far_from_sea_property_sea_proximity_type_text);

    companion object {
        fun fromId(id: Long) = entries.find { it.id == id }
    }
}
