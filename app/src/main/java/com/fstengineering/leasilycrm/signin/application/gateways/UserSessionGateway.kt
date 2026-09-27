package com.fstengineering.leasilycrm.signin.application.gateways

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.signin.application.models.dtos.SignInResultWriteModel

interface UserSessionGateway {
    suspend fun signIn(idToken: String): AppResult<SignInResultWriteModel, ApplicationError>
}
