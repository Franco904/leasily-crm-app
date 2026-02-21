package com.fstengineering.leasilycrm.core.application.useCases.proxies

import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.UseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.SystemError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class RequireAuthUseCaseProxy<P : UseCase.UseCaseParam, R>(
    private val delegate: UseCase<P, R>,
    private val userSessionService: UserSessionService,
) : UseCase<P, R> {
    override suspend fun invoke(params: P): AppResult<R, SystemError> {
        return delegate(params)
            .onError {
                if (it == ApplicationError.Unauthorized) {
                    userSessionService.onSessionExpired()
                }
            }
    }
}
