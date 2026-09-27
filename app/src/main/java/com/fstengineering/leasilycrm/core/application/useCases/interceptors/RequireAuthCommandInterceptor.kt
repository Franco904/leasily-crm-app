package com.fstengineering.leasilycrm.core.application.useCases.interceptors

import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class RequireAuthCommandInterceptor<C : CommandUseCase.CommandModel>(
    private val delegate: CommandUseCase<C>,
    private val userSessionService: UserSessionService,
) : CommandUseCase<C> {
    override suspend fun invoke(command: C): AppResult<Unit, BaseError> {
        return delegate(command)
            .onError { error ->
                if (error == ApplicationError.Unauthorized) {
                    userSessionService.onSessionExpired()
                }
            }
    }
}
