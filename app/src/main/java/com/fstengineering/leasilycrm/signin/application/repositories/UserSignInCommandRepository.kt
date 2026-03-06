package com.fstengineering.leasilycrm.signin.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.signin.domain.entities.User

interface UserSignInCommandRepository {
    suspend fun store(user: User): AppResult<Unit, ApplicationError>
}
