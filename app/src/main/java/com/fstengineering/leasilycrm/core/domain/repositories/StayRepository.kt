package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface StayRepository {
    suspend fun insertOrUpdate(stay: Stay): AppResult<Long, DataSourceError>

    suspend fun delete(id: Long): AppResult<Unit, DataSourceError>

    suspend fun getById(stayId: Long): AppResult<Stay, DataSourceError>

    fun watchById(stayId: Long): Flow<AppResult<Stay?, DataSourceError>>

    fun watchAll(): Flow<AppResult<ImmutableList<Stay>, DataSourceError>>

    fun watchAllByProperty(
        propertyId: Long,
    ): Flow<AppResult<ImmutableList<Stay>, DataSourceError>>

    fun watchAllByTenant(
        tenantId: Long,
    ): Flow<AppResult<ImmutableList<Stay>, DataSourceError>>

    fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean, DataSourceError>>

    suspend fun setMustNotShowCheckOutDialogAnymore(
        mustNotShowCheckOutDialogAnymore: Boolean,
    ): AppResult<Unit, DataSourceError>
}
