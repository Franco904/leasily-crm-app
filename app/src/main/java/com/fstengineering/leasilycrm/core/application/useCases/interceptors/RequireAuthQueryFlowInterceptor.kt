package com.fstengineering.leasilycrm.core.application.useCases.interceptors

import com.fstengineering.leasilycrm.core.application.services.UserSessionService
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class RequireAuthQueryFlowInterceptor<Q : QueryUseCase.Args, R>(
    private val delegate: QueryFlowUseCase<Q, R>,
    private val userSessionService: UserSessionService,
) : QueryFlowUseCase<Q, R> {
    override suspend fun invoke(args: Q): Flow<AppResult<R, ApplicationError>> {
        return delegate(args)
            .onEach { result ->
                result.onError {
                    if (it == ApplicationError.Unauthorized) {
                        userSessionService.onSessionExpired()
                    }
                }
            }
    }
}
