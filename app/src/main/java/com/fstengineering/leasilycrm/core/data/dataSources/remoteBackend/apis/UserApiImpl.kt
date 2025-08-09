package com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis

import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.HttpClientManager
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces.UserApi
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.SignInRequestDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.UpdateUserRequestDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.responseDtos.GetUserResponseDto
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class UserApiImpl(
    private val client: HttpClientManager,
) : UserApi {
    override suspend fun signInUser(
        signInRequestDto: SignInRequestDto,
    ): AppResult<Unit, DataSourceError.RemoteBackendError> {
        return client.request(
            url = "$ROUTE/sign-in",
            method = HttpMethod.Post,
            body = signInRequestDto,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = Unit) },
            )
    }

    override suspend fun getUser(): AppResult<GetUserResponseDto, DataSourceError.RemoteBackendError> {
        return client.request(
            url = ROUTE,
            method = HttpMethod.Get,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { response -> AppResult.Success(data = response.body()) },
            )
    }

    override suspend fun updateUser(
        updateUserRequestDto: UpdateUserRequestDto,
    ): AppResult<Unit, DataSourceError.RemoteBackendError> {
        return client.request(
            url = "$ROUTE/update",
            method = HttpMethod.Put,
            body = updateUserRequestDto,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = Unit) },
            )
    }

    override suspend fun deleteUser(): AppResult<Unit, DataSourceError.RemoteBackendError> {
        return client.request(
            url = "$ROUTE/delete",
            method = HttpMethod.Delete,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = Unit) },
            )
    }

    override suspend fun signOutUser(): AppResult<Unit, DataSourceError.RemoteBackendError> {
        return client.request(
            url = "$ROUTE/sign-out",
            method = HttpMethod.Get,
        )
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = Unit) },
            )
    }

    companion object {
        private const val ROUTE = "/user"
    }
}
