package com.fstengineering.leasilycrm.core.application.useCases.proxies

import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.SystemError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class RequireAuthQueryProxy<Q : QueryUseCase.QueryModel, R : QueryUseCase.ReadModel>(
    private val delegate: QueryUseCase<Q, R>,
    private val userSessionService: UserSessionService,
) : QueryUseCase<Q, R> {
    override suspend fun invoke(query: Q): AppResult<R, SystemError> {
        return delegate(query)
            .onError {
                if (it == ApplicationError.Unauthorized) {
                    userSessionService.onSessionExpired()
                }
            }
    }
}
