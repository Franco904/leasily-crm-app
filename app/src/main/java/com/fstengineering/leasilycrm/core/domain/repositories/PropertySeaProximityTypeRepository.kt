package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableList

interface PropertySeaProximityTypeRepository {
    suspend fun getAll(): AppResult<ImmutableList<PropertySeaProximityType>, DataError>
}
