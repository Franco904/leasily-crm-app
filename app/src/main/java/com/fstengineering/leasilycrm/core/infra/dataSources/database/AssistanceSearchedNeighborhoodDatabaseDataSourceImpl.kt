package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistancePreferredNeighborhoodDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePreferredNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSearchedNeighborhoodDatabaseDataSource

class AssistanceSearchedNeighborhoodDatabaseDataSourceImpl(
    private val dao: AssistancePreferredNeighborhoodDao,
) : AssistanceSearchedNeighborhoodDatabaseDataSource {
    override suspend fun insertOrUpdate(
        neighborhood: AssistancePreferredNeighborhoodEntity,
    ) =
        dao.insertOrUpdate(neighborhood)
}
