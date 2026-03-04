package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePreferredNeighborhoodEntity

interface AssistanceSearchedNeighborhoodDatabaseDataSource {
    suspend fun insertOrUpdate(
        neighborhood: AssistancePreferredNeighborhoodEntity,
    )
}
