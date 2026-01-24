package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity

interface AssistanceSearchedNeighborhoodDatabaseDataSource {
    suspend fun insertOrUpdate(
        neighborhood: AssistanceSearchedNeighborhoodEntity,
    )
}
