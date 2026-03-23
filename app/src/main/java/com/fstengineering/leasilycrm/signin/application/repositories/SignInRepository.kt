package com.fstengineering.leasilycrm.signin.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface SignInRepository {
    suspend fun signIn(idToken: String): AppResult<Unit, ApplicationError>
}
