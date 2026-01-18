package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyCharacteristicPropertyCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristicProperty
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyCharacteristicPropertyRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class PropertyCharacteristicPropertyRepositoryImpl(
    private val databaseDataSource: PropertyCharacteristicPropertyCrossRefDatabaseDataSource,
) : PropertyCharacteristicPropertyRepository {
    override suspend fun insertOrUpdate(
        characteristicProperty: PropertyCharacteristicProperty,
    ): AppResult<Unit, DataError> {
        val characteristicPropertyEntity = PropertyCharacteristicPropertyCrossRefEntity.fromDomain(
            domain = characteristicProperty,
        )

        databaseDataSource.insertOrUpdate(characteristicPropertyEntity)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
