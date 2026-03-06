package com.fstengineering.leasilycrm.core.infra.utils

import com.fstengineering.leasilycrm.core.application.useCases.interceptors.RequireAuthCommandInterceptor
import com.fstengineering.leasilycrm.core.application.useCases.interceptors.RequireAuthQueryInterceptor
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

inline fun <reified C> Module.authCommandUseCase(
    qualifier: Qualifier,
    crossinline factory: Scope.() -> C,
) where C : CommandUseCase<*> {
    factory<C>(qualifier) { factory() }

    factory<C> {
        RequireAuthCommandInterceptor(
            delegate = get<C>(qualifier),
            userSessionService = get(),
        ) as C
    }
}

inline fun <reified Q> Module.authQueryUseCase(
    qualifier: Qualifier,
    crossinline factory: Scope.() -> Q,
) where Q : QueryUseCase<*, *> {
    factory<Q>(qualifier) { factory() }

    factory<Q> {
        RequireAuthQueryInterceptor(
            delegate = get<Q>(qualifier),
            userSessionService = get(),
        ) as Q
    }
}
