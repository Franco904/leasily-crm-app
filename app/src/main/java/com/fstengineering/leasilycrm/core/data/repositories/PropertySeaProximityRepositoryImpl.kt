package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertySeaProximityTypeDao
import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.domain.repositories.PropertySeaProximityTypeRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class PropertySeaProximityRepositoryImpl(
    private val dao: PropertySeaProximityTypeDao,
) : PropertySeaProximityTypeRepository {
    override suspend fun getAll(): AppResult<ImmutableList<PropertySeaProximityType>, DataSourceError> {
        val seaProximityTypes = dao.getAll()
            .map { it.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = seaProximityTypes)
    }
}
