package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.TenantContactOriginType
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList

interface TenantContactOriginTypeRepository {
    suspend fun getAll(): AppResult<ImmutableList<TenantContactOriginType>, DataSourceError>
}
