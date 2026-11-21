package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.toDomainErrors
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

sealed class ImovelSeaProximityType(val id: Long) {
    data object SeaAccess : ImovelSeaProximityType(SEA_ACCESS_ID)

    data object SeaView : ImovelSeaProximityType(SEA_VIEW_ID)

    data object FarFromSea : ImovelSeaProximityType(FAR_FROM_SEA_ID)

    class DistanceToSea private constructor(val distance: AppDecimal) : ImovelSeaProximityType(
        DISTANCE_TO_SEA_ID,
    ) {
        companion object {
            fun parse(
                distance: AppDecimal?,
            ): AppResult<DistanceToSea> {
                if (distance == null) {
                    return AppResult.Error(
                        ValidationErrors(persistentListOf(DomainError.ImovelSeaProximityDistanceToSea.Required))
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
        ): AppResult<ImovelSeaProximityType> {
            val errors = mutableListOf<DomainError>()

            val parsed = when (id) {
                SEA_ACCESS_ID -> SeaAccess
                SEA_VIEW_ID -> SeaView
                FAR_FROM_SEA_ID -> FarFromSea

                DISTANCE_TO_SEA_ID -> {
                    DistanceToSea.parse(distanceToSea)
                        .fold(onError = { errors.addAll(it.toDomainErrors()); null }, onSuccess = { it })
                }

                else -> {
                    errors.add(DomainError.ImovelSeaProximity.ImovelSeaProximityInvalid)
                    null
                }
            }

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            return AppResult.Success(data = requireNotNull(parsed))
        }
    }
}
