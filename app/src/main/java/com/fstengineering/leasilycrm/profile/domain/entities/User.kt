package com.fstengineering.leasilycrm.profile.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.profile.domain.valueObjects.Creci

class User private constructor(val creci: Creci?) {
    companion object {
        fun parse(creciNumber: String?): AppResult<User, DomainError> {
            val creci = creciNumber?.let { Creci.parse(creciNumber) }
                ?.fold(
                    onError = { return AppResult.Error(error = it) },
                    onSuccess = { it },
                )

            val parsed = User(creci = creci)

            return AppResult.Success(data = parsed)
        }
    }
}
