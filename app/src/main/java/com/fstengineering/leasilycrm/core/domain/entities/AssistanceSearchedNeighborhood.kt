package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class AssistanceSearchedNeighborhood private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val neighborhood: String,
    val isDeleted: Boolean,
) {
    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            updateDateTime: AppDateTime?,
            neighborhood: String?,
            isDeleted: Boolean,
        ): AssistanceSearchedNeighborhood {
            validate(neighborhood)
                .onError { throw it.asException() }

            return AssistanceSearchedNeighborhood(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                neighborhood = requireNotNull(neighborhood),
                isDeleted = isDeleted,
            )
        }

        fun validate(neighborhood: String?): AppResult<Unit, ParsingError.Assistance> {
            return when {
                neighborhood.isNullOrBlank() -> AppResult.Error(ParsingError.Assistance.SearchedNeighborhoodNameRequired)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
