package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validations.validateDistanceToSea

sealed class PropertySeaProximityType(val id: Long) {
    data object SeaAccess : PropertySeaProximityType(SEA_ACCESS_ID)
    data object SeaView : PropertySeaProximityType(SEA_VIEW_ID)
    data object FarFromSea : PropertySeaProximityType(FAR_FROM_SEA_ID)
    class DistanceToSea private constructor(val distance: AppDecimal) : PropertySeaProximityType(
        DISTANCE_TO_SEA_ID,
    ) {
        companion object {
            fun parse(distance: AppDecimal?): DistanceToSea {
                validateDistanceToSea(distance)
                    .onError { throw it.asException() }

                return DistanceToSea(requireNotNull(distance))
            }
        }
    }

    companion object {
        const val SEA_ACCESS_ID = 1L
        const val SEA_VIEW_ID = 2L
        const val FAR_FROM_SEA_ID = 3L
        const val DISTANCE_TO_SEA_ID = 4L

        fun parse(id: Long, distanceToSea: AppDecimal?): PropertySeaProximityType = when (id) {
            SEA_ACCESS_ID -> SeaAccess
            SEA_VIEW_ID -> SeaView
            FAR_FROM_SEA_ID -> FarFromSea
            DISTANCE_TO_SEA_ID -> DistanceToSea.parse(distanceToSea)
            else -> throw ParsingError.PropertySeaProximity.PropertySeaProximityInvalid.asException()
        }
    }
}
