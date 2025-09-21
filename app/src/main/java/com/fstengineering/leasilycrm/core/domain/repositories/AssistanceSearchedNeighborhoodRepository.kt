package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

interface AssistanceSearchedNeighborhoodRepository {
    suspend fun insertOrUpdate(
        searchedNeighborhood: AssistanceSearchedNeighborhood,
    ): AppResult<Unit, DataSourceError>
}
