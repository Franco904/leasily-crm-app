package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError

sealed class PropertySeaProximityType(val id: Long) {
    data object SeaAccess : PropertySeaProximityType(SEA_ACCESS_ID)
    data object SeaView : PropertySeaProximityType(SEA_VIEW_ID)
    data object FarFromSea : PropertySeaProximityType(FAR_FROM_SEA_ID)
    class DistanceToSea private constructor(val distance: AppDecimal) : PropertySeaProximityType(
        DISTANCE_TO_SEA_ID,
    ) {
        companion object {
            fun parse(distance: AppDecimal?): DistanceToSea {
                validate(distance)
                    .onError { throw it.asException() }

                return DistanceToSea(requireNotNull(distance))
            }

            fun validate(distance: AppDecimal?): AppResult<Unit, DomainError> {
                return when {
                    distance == null -> AppResult.Error(DomainError.PropertySeaProximity.DistanceToSeaRequired)
                    distance.isNegative -> AppResult.Error(DomainError.PropertySeaProximity.DistanceToSeaIsNegative)
                    else -> AppResult.Success(Unit)
                }
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
            else -> throw DomainError.PropertySeaProximity.PropertySeaProximityInvalid.asException()
        }

        fun validate(id: Long, distanceToSea: AppDecimal?): AppResult<Unit, DomainError> {
            val noValidationTypes = listOf(
                SEA_ACCESS_ID,
                SEA_VIEW_ID,
                FAR_FROM_SEA_ID,
            )

            return when (id) {
                in noValidationTypes -> AppResult.Success(Unit)
                DISTANCE_TO_SEA_ID -> DistanceToSea.validate(distanceToSea)
                else -> AppResult.Error(DomainError.PropertySeaProximity.PropertySeaProximityInvalid)
            }
        }
    }
}
