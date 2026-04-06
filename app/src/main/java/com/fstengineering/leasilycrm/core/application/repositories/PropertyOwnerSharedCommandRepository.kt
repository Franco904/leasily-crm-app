package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerSharedCommandRepository {
    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>

    fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwner>, ApplicationError>>
}
