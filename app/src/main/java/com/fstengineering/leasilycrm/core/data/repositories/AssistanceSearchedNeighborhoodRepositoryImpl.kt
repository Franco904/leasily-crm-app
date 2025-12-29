package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceSearchedNeighborhoodDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceSearchedNeighborhoodRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class AssistanceSearchedNeighborhoodRepositoryImpl(
    private val dao: AssistanceSearchedNeighborhoodDao,
) : AssistanceSearchedNeighborhoodRepository {
    override suspend fun insertOrUpdate(
        searchedNeighborhood: AssistanceSearchedNeighborhood,
    ): AppResult<Unit, DataError> {
        val searchedNeighborhoodEntity =
            AssistanceSearchedNeighborhoodEntity.fromDomain(domain = searchedNeighborhood)

        dao.insertOrUpdate(searchedNeighborhoodEntity)

        return AppResult.Success(data = Unit)
    }
}
