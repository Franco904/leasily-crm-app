package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

sealed class PropertySeaProximityType(val id: Long) {
    data object SeaAccess : PropertySeaProximityType(SEA_ACCESS_ID)

    data object SeaView : PropertySeaProximityType(SEA_VIEW_ID)

    data object FarFromSea : PropertySeaProximityType(FAR_FROM_SEA_ID)

    class DistanceToSea private constructor(val distance: AppDecimal) : PropertySeaProximityType(
        DISTANCE_TO_SEA_ID,
    ) {
        companion object {
            fun parse(
                distance: AppDecimal?,
            ): AppResult<DistanceToSea, ValidationErrors> {
                if (distance == null) {
                    return AppResult.Error(
                        ValidationErrors(persistentListOf(DomainError.PropertySeaProximityDistanceToSea.Required))
                    )
                }

                return AppResult.Success(data = DistanceToSea(AppDecimal(distance.decimal.abs())))
            }
        }
    }

    companion object {
        const val SEA_ACCESS_ID = 1L
        const val SEA_VIEW_ID = 2L
        const val FAR_FROM_SEA_ID = 3L
        const val DISTANCE_TO_SEA_ID = 4L

        fun parse(
            id: Long,
            distanceToSea: AppDecimal?,
        ): AppResult<PropertySeaProximityType, ValidationErrors> {
            val errors = mutableListOf<DomainError>()

            val parsed = when (id) {
                SEA_ACCESS_ID -> SeaAccess
                SEA_VIEW_ID -> SeaView
                FAR_FROM_SEA_ID -> FarFromSea

                DISTANCE_TO_SEA_ID -> {
                    DistanceToSea.parse(distanceToSea)
                        .fold(onError = { errors.addAll(it.errors); null }, onSuccess = { it })
                }

                else -> {
                    errors.add(DomainError.PropertySeaProximity.PropertySeaProximityInvalid)
                    null
                }
            }

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            return AppResult.Success(data = requireNotNull(parsed))
        }
    }
}
