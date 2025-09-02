package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyCharacteristicPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristicProperty
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyCharacteristicPropertyRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

class PropertyCharacteristicPropertyRepositoryImpl(
    private val dao: PropertyCharacteristicPropertyCrossRefDao,
) : PropertyCharacteristicPropertyRepository {
    override suspend fun insertOrUpdate(
        characteristicProperty: PropertyCharacteristicProperty,
    ): AppResult<Unit, DataSourceError> {
        val characteristicPropertyEntity = PropertyCharacteristicPropertyCrossRefEntity.fromDomain(
            domain = characteristicProperty,
        )

        dao.insertOrUpdate(characteristicPropertyEntity)

        return AppResult.Success(data = Unit)
    }
}
