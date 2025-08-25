package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CustomerSegmentTypeDao
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentTypeRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class CustomerSegmentTypeRepositoryImpl(
    private val dao: CustomerSegmentTypeDao,
) : CustomerSegmentTypeRepository {
    override suspend fun getAll(): AppResult<ImmutableList<CustomerSegmentType>, DataSourceError> {
        val customerSegmentTypes = dao.getAll()
            .map { it.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = customerSegmentTypes)
    }
}
