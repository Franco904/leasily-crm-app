package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CustomerSegmentDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.CustomerSegmentCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.CustomerSegmentDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

class CustomerSegmentDatabaseDataSourceImpl(
    private val dao: CustomerSegmentDao,
) : CustomerSegmentDatabaseDataSource {
    override suspend fun insertOrUpdate(
        customerSegment: CustomerSegmentEntity,
    ): AppResult<Unit, DataError> =
        onDatabase { dao.insertOrUpdate(customerSegment) }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> =
        onDatabase { dao.delete(id) }

    override suspend fun getById(id: Long): AppResult<CustomerSegmentEntity, DataError> =
        onDatabase { dao.getById(id) }

    override suspend fun getAllTenantType(
        userId: Long,
    ): AppResult<List<CustomerSegmentCompound>, DataError> =
        onDatabase { dao.getAllTenantType(userId) }

    override suspend fun getAllPropertyOwnerType(
        userId: Long,
    ): AppResult<List<CustomerSegmentCompound>, DataError> =
        onDatabase { dao.getAllPropertyOwnerType(userId) }

    override fun watchAll(
        userId: Long,
    ): AppResult<Flow<List<CustomerSegmentCompound>>, DataError> =
        onDatabase { dao.watchAll(userId) }

    override suspend fun countCustomerSegmentsWithName(
        userId: Long,
        customerSegmentName: String,
        customerSegmentTypeId: Long,
        currentStoredCustomerSegmentName: String?,
    ): AppResult<Int, DataError> =
        onDatabase {
            dao.countCustomerSegmentsWithName(
                userId,
                customerSegmentName,
                customerSegmentTypeId,
                currentStoredCustomerSegmentName,
            )
        }
}
