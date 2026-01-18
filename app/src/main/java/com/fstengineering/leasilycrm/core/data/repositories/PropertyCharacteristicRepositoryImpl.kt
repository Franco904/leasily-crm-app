package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyCharacteristicDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class PropertyCharacteristicRepositoryImpl(
    private val databaseDataSource: PropertyCharacteristicDatabaseDataSource,
) : PropertyCharacteristicRepository {
    override suspend fun getAll(): AppResult<ImmutableList<PropertyCharacteristic>, DataError> {
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
