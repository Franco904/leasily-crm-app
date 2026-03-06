package com.fstengineering.leasilycrm.core.application.useCases.interceptors

import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.onErrors

class RequireAuthCommandInterceptor<C : CommandUseCase.CommandModel>(
    private val delegate: CommandUseCase<C>,
    private val userSessionService: UserSessionService,
) : CommandUseCase<C> {
    override suspend fun invoke(command: C): AppResultCompound<Unit, ApplicationError> {
        return delegate(command)
            .onErrors { errors ->
                if (errors.any { it == ApplicationError.Infra.Unauthorized }) {
                    userSessionService.onSessionExpired()
                }
            }
    }
}
