package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CustomerSegmentTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.CustomerSegmentTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class CustomerSegmentTypeDatabaseDataSourceImpl(
    private val dao: CustomerSegmentTypeDao,
) : CustomerSegmentTypeDatabaseDataSource {
    override suspend fun insertOrUpdate(
        customerSegmentType: CustomerSegmentTypeEntity,
    ): AppResult<Unit, DataError> =
        onDatabase { dao.insertOrUpdate(customerSegmentType) }

    override suspend fun getAll(): AppResult<List<CustomerSegmentTypeEntity>, DataError> =
        onDatabase { dao.getAll() }
}
