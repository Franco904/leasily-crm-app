package com.fstengineering.leasilycrm.signin.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.signin.application.models.SignInCommandModel
import com.fstengineering.leasilycrm.signin.application.repositories.SignInRepository
import com.fstengineering.leasilycrm.signin.application.repositories.UserSignInCommandRepository
import kotlinx.collections.immutable.persistentListOf

class SignInUserUseCase(
    private val signInRepository: SignInRepository,
    private val userSignInCommandRepository: UserSignInCommandRepository,
) : CommandUseCase<SignInCommandModel> {
    override suspend fun invoke(
        command: SignInCommandModel,
    ): AppResultCompound<Unit, ApplicationError> {
        val user = signInRepository.signIn(idToken = command.idToken)
            .fold(
                onError = { e -> return AppResultCompound.Errors(errors = persistentListOf(e)) },
                onSuccess = { user -> user },
            )

        userSignInCommandRepository.store(user)
            .onError { e -> return AppResultCompound.Errors(errors = persistentListOf(e)) }

        return AppResultCompound.Success(data = Unit)
    }
}