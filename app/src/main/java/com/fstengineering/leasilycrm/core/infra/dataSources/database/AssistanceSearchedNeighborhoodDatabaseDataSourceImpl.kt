package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceSearchedNeighborhoodDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSearchedNeighborhoodDatabaseDataSource

class AssistanceSearchedNeighborhoodDatabaseDataSourceImpl(
    private val dao: AssistanceSearchedNeighborhoodDao,
) : AssistanceSearchedNeighborhoodDatabaseDataSource {
    override suspend fun insertOrUpdate(
        neighborhood: AssistanceSearchedNeighborhoodEntity,
    ) =
        dao.insertOrUpdate(neighborhood)
}
