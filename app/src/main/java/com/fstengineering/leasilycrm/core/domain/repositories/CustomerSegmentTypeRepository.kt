package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableList

interface CustomerSegmentTypeRepository {
    suspend fun getAll(): AppResult<ImmutableList<CustomerSegmentType>, DataError>
}
