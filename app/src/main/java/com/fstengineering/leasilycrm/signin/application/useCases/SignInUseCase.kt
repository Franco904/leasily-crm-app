package com.fstengineering.leasilycrm.signin.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.signin.application.gateways.UserSessionGateway
import com.fstengineering.leasilycrm.signin.application.models.commands.IdToken
import com.fstengineering.leasilycrm.signin.application.stores.UserSessionStore
import kotlinx.collections.immutable.persistentListOf

class SignInUseCase(
    private val userSessionGateway: UserSessionGateway,
    private val userSessionStore: UserSessionStore,
) : CommandUseCase<IdToken> {
    override suspend fun invoke(command: IdToken): AppResultCompound<Unit, BusinessRuleError> {
        val signInResultWriteModel = userSessionGateway.signIn(idToken = command.idToken)
            .fold(
                onError = { return AppResultCompound.Errors(persistentListOf(it)) },
                onSuccess = { it },
            )

        userSessionStore.storeSignedInUser(signInResult = signInResultWriteModel)

        return AppResultCompound.Success(data = Unit)
    }
}
