package com.fstengineering.leasilycrm.profile.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.profile.domain.valueObjects.Creci
import kotlinx.collections.immutable.toPersistentList

class User private constructor(val creci: Creci?) {
    companion object {
        fun parse(creciNumber: String?): AppResult<User, ValidationErrors> {
            val errors = mutableListOf<DomainError>()

            val creci = creciNumber?.let { Creci.parse(it) }
                ?.fold(onError = { errors.add(it); null }, onSuccess = { it })

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = User(creci = creci)

            return AppResult.Success(data = parsed)
        }
    }
}
