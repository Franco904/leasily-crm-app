package com.fstengineering.leasilycrm.signin.infra.gateways

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.api.interfaces.UserApi
import com.fstengineering.leasilycrm.core.infra.api.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.signin.application.gateways.UserSessionGateway
import com.fstengineering.leasilycrm.signin.application.models.dtos.SignInResultWriteModel
import com.fstengineering.leasilycrm.signin.infra.mappers.toWriteModel

class UserSessionGatewayImpl(
    private val remoteDataSource: UserApi,
) : UserSessionGateway {
    override suspend fun signIn(idToken: String): AppResult<SignInResultWriteModel> {
        val signInRequestDto = SignInRequestDto(idToken = idToken)
        val signInResponseDto = remoteDataSource.signInUser(signInRequestDto = signInRequestDto)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { responseDto -> responseDto }
            )

        return AppResult.Success(data = signInResponseDto.toWriteModel())
    }
}
