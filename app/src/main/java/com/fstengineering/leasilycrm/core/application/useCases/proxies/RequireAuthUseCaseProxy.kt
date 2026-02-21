package com.fstengineering.leasilycrm.core.application.useCases.proxies

import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.UseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.SystemError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class RequireAuthUseCaseProxy<W : UseCase.WriteModel, R>(
    private val delegate: UseCase<W, R>,
    private val userSessionService: UserSessionService,
) : UseCase<W, R> {
    override suspend fun invoke(writeModel: W): AppResult<R, SystemError> {
        return delegate(writeModel)
            .onError {
                if (it == ApplicationError.Unauthorized) {
                    userSessionService.onSessionExpired()
                }
            }
    }
}
