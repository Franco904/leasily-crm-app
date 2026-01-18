package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CustomerSegmentDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.CustomerSegmentDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

class CustomerSegmentDatabaseDataSourceImpl(
    private val dao: CustomerSegmentDao,
) : CustomerSegmentDatabaseDataSource {
    override suspend fun insertOrUpdate(
        customerSegment: CustomerSegmentEntity,
    ) =
        dao.insertOrUpdate(customerSegment)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getAll(
        userId: Long,
    ): AppResult<List<CustomerSegmentEntity>, DataError> =
        onDatabaseSafe { dao.getAll(userId) }
}
