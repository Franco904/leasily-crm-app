package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.PropertyType
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList

interface PropertyTypeRepository {
    suspend fun getAll(): AppResult<ImmutableList<PropertyType>, DataSourceError>
}
