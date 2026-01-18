package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.CustomerSegmentTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentTypeRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class CustomerSegmentTypeRepositoryImpl(
    private val databaseDataSource: CustomerSegmentTypeDatabaseDataSource,
) : CustomerSegmentTypeRepository {
    override suspend fun getAll(): AppResult<ImmutableList<CustomerSegmentType>, DataError> {
        val customerSegmentTypes = databaseDataSource.getAll()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it }
            )
            .map { it.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = customerSegmentTypes)
    }
}
