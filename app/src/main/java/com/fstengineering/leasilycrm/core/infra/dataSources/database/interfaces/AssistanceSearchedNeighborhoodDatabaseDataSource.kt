package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity

interface AssistanceSearchedNeighborhoodDatabaseDataSource {
    suspend fun insertOrUpdate(
        neighborhood: AssistanceSearchedNeighborhoodEntity,
    )
}
