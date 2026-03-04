package com.fstengineering.leasilycrm.core.application.useCases.proxies

import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.SystemError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class RequireAuthCommandProxy<W : CommandUseCase.CommandModel>(
    private val delegate: CommandUseCase<W>,
    private val userSessionService: UserSessionService,
) : CommandUseCase<W> {
    override suspend fun invoke(command: W): AppResult<Unit, SystemError> {
        return delegate(command)
            .onError {
                if (it == ApplicationError.Unauthorized) {
                    userSessionService.onSessionExpired()
                }
            }
    }
}
