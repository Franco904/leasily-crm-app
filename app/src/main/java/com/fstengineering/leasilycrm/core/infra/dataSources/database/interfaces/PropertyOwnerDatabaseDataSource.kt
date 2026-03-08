package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyOwnerCompound
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerDatabaseDataSource {
    suspend fun insertOrUpdate(propertyOwner: PropertyOwnerEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<PropertyOwnerCompound, InfraError>

    fun watchById(id: Long): AppResult<Flow<PropertyOwnerCompound>, InfraError>

    fun watchAll(): AppResult<Flow<List<PropertyOwnerCompound>>, InfraError>
}
