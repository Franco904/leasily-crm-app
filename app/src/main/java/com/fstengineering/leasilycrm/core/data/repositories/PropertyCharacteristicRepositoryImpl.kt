package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyCharacteristicDao
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class PropertyCharacteristicRepositoryImpl(
    private val dao: PropertyCharacteristicDao,
) : PropertyCharacteristicRepository {
    override suspend fun getAll(): AppResult<ImmutableList<PropertyCharacteristic>, DataSourceError> {
        val seaProximityTypes = dao.getAll()
            .map { it.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = seaProximityTypes)
    }
}
