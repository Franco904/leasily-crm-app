package com.fstengineering.leasilycrm.signin.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.signin.application.gateways.UserSessionGateway
import com.fstengineering.leasilycrm.signin.application.models.commands.IdToken
import com.fstengineering.leasilycrm.signin.application.stores.UserSessionStore

class SignInUseCase(
    private val userSessionGateway: UserSessionGateway,
    private val userSessionStore: UserSessionStore,
) {
    suspend operator fun invoke(command: IdToken): AppResult<Unit, BaseError> {
        val signInResultWriteModel = userSessionGateway.signIn(idToken = command.idToken)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )

        userSessionStore.storeSignedInUser(signInResult = signInResultWriteModel)

        return AppResult.Success(data = Unit)
    }
}
