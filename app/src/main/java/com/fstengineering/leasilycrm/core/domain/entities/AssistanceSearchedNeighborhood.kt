package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validations.validateAssistanceSearchedNeighborhood

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
            validateAssistanceSearchedNeighborhood(neighborhood)
                .onError { throw it.asException() }

            return AssistanceSearchedNeighborhood(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                neighborhood = requireNotNull(neighborhood),
                isDeleted = isDeleted,
            )
        }
    }
}
