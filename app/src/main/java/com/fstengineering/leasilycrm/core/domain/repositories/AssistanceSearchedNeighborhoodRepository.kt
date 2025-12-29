package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface AssistanceSearchedNeighborhoodRepository {
    suspend fun insertOrUpdate(
        searchedNeighborhood: AssistanceSearchedNeighborhood,
    ): AppResult<Unit, DataError>
}
