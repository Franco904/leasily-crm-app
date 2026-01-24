package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertySeaProximityTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.domain.repositories.PropertySeaProximityTypeRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class PropertySeaProximityRepositoryImpl(
    private val databaseDataSource: PropertySeaProximityTypeDatabaseDataSource,
) : PropertySeaProximityTypeRepository {
    override suspend fun getAll(): AppResult<ImmutableList<PropertySeaProximityType>, DataError> {
        val seaProximityTypes = databaseDataSource.getAll()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it }
            )
            .map { it.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = seaProximityTypes)
    }
}
