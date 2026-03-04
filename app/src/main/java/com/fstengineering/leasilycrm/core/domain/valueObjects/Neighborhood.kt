package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

class Neighborhood private constructor(val value: String) {
    companion object {
        fun parse(neighborhood: String?): AppResult<Neighborhood, DomainError.Neighborhood> {
            return when {
                neighborhood.isNullOrBlank() -> AppResult.Error(DomainError.Neighborhood.NeighborhoodRequired)
                else -> AppResult.Success(data = Neighborhood(neighborhood))
            }
        }
    }
}
