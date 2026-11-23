package com.fstengineering.leasilycrm.imovelRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import kotlinx.collections.immutable.toPersistentList

class ImovelFile private constructor(
    val id: Long?,
    val uriString: String,
    val isMainPicture: Boolean,
) {
    companion object {
        fun parse(
            id: Long? = null,
            uriString: String,
            isMainPicture: Boolean,
        ): AppResult<ImovelFile> {
            if (uriString.isBlank()) {
                return AppResult.Error(
                    ValidationErrors(listOf(DomainError.ImovelFileUri.Required).toPersistentList())
                )
            }

            return AppResult.Success(
                data = ImovelFile(id = id, uriString = uriString, isMainPicture = isMainPicture)
            )
        }
    }
}
