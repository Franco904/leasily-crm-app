package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceSearchedNeighborhoodDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceSearchedNeighborhoodDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class AssistanceSearchedNeighborhoodDatabaseDataSourceImpl(
    private val dao: AssistanceSearchedNeighborhoodDao,
) : AssistanceSearchedNeighborhoodDatabaseDataSource {
    override suspend fun insertOrUpdate(
        neighborhood: AssistanceSearchedNeighborhoodEntity,
    ) =
        dao.insertOrUpdate(neighborhood)
}
