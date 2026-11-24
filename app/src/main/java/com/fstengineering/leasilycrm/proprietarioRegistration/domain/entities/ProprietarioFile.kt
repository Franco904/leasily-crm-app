package com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import kotlinx.collections.immutable.toPersistentList

class ProprietarioFile private constructor(
    val id: Long?,
    val uriString: String,
    val name: String,
) {
    companion object {
        fun parse(
            id: Long? = null,
            uriString: String,
            name: String,
        ): AppResult<ProprietarioFile> {
            val errors = mutableListOf<DomainError>()

            if (uriString.isBlank()) errors.add(DomainError.ProprietarioFileUri.Required)
            if (name.isBlank()) errors.add(DomainError.ProprietarioFileName.Required)

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            return AppResult.Success(
                data = ProprietarioFile(
                    id = id,
                    uriString = uriString,
                    name = name
                )
            )
        }
    }
}
