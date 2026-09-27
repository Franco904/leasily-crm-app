package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyOwnerCompound
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerDatabaseDataSource {
    suspend fun insertOrUpdate(propertyOwner: PropertyOwnerEntity): Long

    suspend fun delete(id: Long)

    suspend fun getNameById(id: Long): AppResult<String, ApplicationError>

    suspend fun getByIdCompound(id: Long): AppResult<PropertyOwnerCompound, ApplicationError>

    fun watchById(id: Long): AppResult<Flow<PropertyOwnerCompound>, ApplicationError>

    fun watchAll(): AppResult<Flow<List<PropertyOwnerEntity>>, ApplicationError>

    fun watchAllCompound(): AppResult<Flow<List<PropertyOwnerCompound>>, ApplicationError>
}
