package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSearchedNeighborhoodEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceSearchedNeighborhoodDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceSearchedNeighborhoodRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class AssistanceSearchedNeighborhoodRepositoryImpl(
    private val databaseDataSource: AssistanceSearchedNeighborhoodDatabaseDataSource,
) : AssistanceSearchedNeighborhoodRepository {
    override suspend fun insertOrUpdate(
        searchedNeighborhood: AssistanceSearchedNeighborhood,
    ): AppResult<Unit, DataError> {
        val searchedNeighborhoodEntity =
            AssistanceSearchedNeighborhoodEntity.fromDomain(domain = searchedNeighborhood)

        databaseDataSource.insertOrUpdate(searchedNeighborhoodEntity)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
