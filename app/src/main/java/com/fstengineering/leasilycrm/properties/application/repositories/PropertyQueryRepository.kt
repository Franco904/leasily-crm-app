package com.fstengineering.leasilycrm.properties.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.properties.application.models.dtos.PropertyReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertyQueryRepository {
    suspend fun getAllMatching(matching: String?): AppResult<ImmutableList<PropertyReadModel>, ApplicationError>

    fun watchAll(): Flow<AppResult<ImmutableList<PropertyReadModel>, ApplicationError>>
}
