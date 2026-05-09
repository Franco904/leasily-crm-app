package com.fstengineering.leasilycrm.core.infra.utils

import com.fstengineering.leasilycrm.core.application.useCases.interceptors.RequireAuthCommandInterceptor
import com.fstengineering.leasilycrm.core.application.useCases.interceptors.RequireAuthQueryFlowInterceptor
import com.fstengineering.leasilycrm.core.application.useCases.interceptors.RequireAuthQueryInterceptor
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

@Suppress("UNCHECKED_CAST")
inline fun <reified C> Module.authCommandUseCase(
    qualifier: Qualifier,
    crossinline factory: Scope.() -> C,
) where C : CommandUseCase<*> {
    factory<C>(qualifier) { factory() }

    factory<C> {
        RequireAuthCommandInterceptor(
            delegate = get<C>(qualifier) as CommandUseCase<CommandUseCase.CommandModel>,
            userSessionService = get(),
        ) as C
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified Q> Module.authQueryUseCase(
    qualifier: Qualifier,
    crossinline factory: Scope.() -> Q,
) where Q : QueryUseCase<*, *> {
    factory<Q>(qualifier) { factory() }

    factory<Q> {
        RequireAuthQueryInterceptor(
            delegate = get<Q>(qualifier) as QueryUseCase<QueryUseCase.Args, Any>,
            userSessionService = get(),
        ) as Q
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified Q> Module.authQueryFlowUseCase(
    qualifier: Qualifier,
    crossinline factory: Scope.() -> Q,
) where Q : QueryFlowUseCase<*, *> {
    factory<Q>(qualifier) { factory() }

    factory<Q> {
        RequireAuthQueryFlowInterceptor(
            delegate = get<Q>(qualifier) as QueryFlowUseCase<QueryUseCase.Args, Any>,
            userSessionService = get(),
        ) as Q
    }
}
