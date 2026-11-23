package com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import kotlinx.collections.immutable.toPersistentList

class StayFile private constructor(
    val id: Long?,
    val uriString: String,
    val name: String,
) {
    companion object {
        fun parse(
            id: Long? = null,
            uriString: String,
            name: String,
        ): AppResult<StayFile> {
            val errors = mutableListOf<DomainError>()

            if (uriString.isBlank()) errors.add(DomainError.StayFileUri.Required)
            if (name.isBlank()) errors.add(DomainError.StayFileName.Required)

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            return AppResult.Success(data = StayFile(id = id, uriString = uriString, name = name))
        }
    }
}
