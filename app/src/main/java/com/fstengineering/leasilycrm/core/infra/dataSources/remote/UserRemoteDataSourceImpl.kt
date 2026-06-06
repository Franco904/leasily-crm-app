package com.fstengineering.leasilycrm.core.infra.dataSources.remote

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.constants.SIGN_IN_ROUTE
import com.fstengineering.leasilycrm.core.infra.constants.SIGN_OUT_ROUTE
import com.fstengineering.leasilycrm.core.infra.constants.UPDATE_USER_ROUTE
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ExceptionLogger
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces.UserRemoteDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.responseDtos.user.SignInUserResponseDto
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class UserRemoteDataSourceImpl(
    private val client: HttpClient,
    private val exceptionLogger: ExceptionLogger,
) : UserRemoteDataSource {
    override suspend fun signInUser(
        signInRequestDto: SignInRequestDto,
    ): AppResult<SignInUserResponseDto, InfraError> {
        return client.requestBackend(
            url = SIGN_IN_ROUTE,
            method = HttpMethod.Post,
            exceptionLogger = exceptionLogger,
            body = signInRequestDto,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { response -> AppResult.Success(data = response.body()) },
            )
    }

    override suspend fun signOutUser(): AppResult<Unit, InfraError> {
        return client.requestBackend(
            url = SIGN_OUT_ROUTE,
            method = HttpMethod.Get,
            exceptionLogger = exceptionLogger,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = Unit) },
            )
    }

    override suspend fun updateUser(
        updateUserDto: UpdateUserDto,
    ): AppResult<Unit, InfraError> {
        return client.requestBackend(
            url = UPDATE_USER_ROUTE,
            method = HttpMethod.Put,
            exceptionLogger = exceptionLogger,
            body = updateUserDto,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = Unit) },
            )
    }
}
