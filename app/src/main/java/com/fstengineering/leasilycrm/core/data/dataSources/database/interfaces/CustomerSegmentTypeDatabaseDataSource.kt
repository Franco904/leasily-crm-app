package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentTypeEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface CustomerSegmentTypeDatabaseDataSource {
    suspend fun insertOrUpdate(customerSegmentType: CustomerSegmentTypeEntity): AppResult<Unit, DataError>

    suspend fun getAll(): AppResult<List<CustomerSegmentTypeEntity>, DataError>
}
