package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface AssistanceSearchedNeighborhoodDatabaseDataSource {
    suspend fun insertOrUpdate(
        neighborhood: AssistanceSearchedNeighborhoodEntity,
    )
}
