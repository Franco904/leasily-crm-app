package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.PropertyType
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyTypeRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class PropertyTypeRepositoryImpl(
    private val databaseDataSource: PropertyTypeDatabaseDataSource,
) : PropertyTypeRepository {
    override suspend fun getAll(): AppResult<ImmutableList<PropertyType>, DataError> {
        val propertyTypes = databaseDataSource.getAll()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .map { it.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = propertyTypes)
    }
}
