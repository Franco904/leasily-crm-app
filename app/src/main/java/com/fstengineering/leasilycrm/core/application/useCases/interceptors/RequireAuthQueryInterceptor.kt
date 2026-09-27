package com.fstengineering.leasilycrm.core.application.useCases.interceptors

import com.fstengineering.leasilycrm.core.application.services.UserSessionService
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class RequireAuthQueryInterceptor<Q : QueryUseCase.Args, R>(
    private val delegate: QueryUseCase<Q, R>,
    private val userSessionService: UserSessionService,
) : QueryUseCase<Q, R> {
    override suspend fun invoke(args: Q): AppResult<R, ApplicationError> {
        return delegate(args)
            .onError {
                if (it == ApplicationError.Unauthorized) {
                    userSessionService.onSessionExpired()
                }
            }
    }
}
