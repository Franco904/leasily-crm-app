package com.fstengineering.leasilycrm.core.infra.adapters

import android.app.Activity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.CredentialManagerAdapter
import com.fstengineering.leasilycrm.core.infra.utils.CryptoUtils
import com.fstengineering.leasilycrm.core.infra.utils.onBoundary
import com.fstengineering.leasilycrm.core.infra.utils.toApplicationError
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class CredentialManagerAdapterImpl(
    private val connectivityAdapter: ConnectivityAdapter,
    private val currentActivityTracker: CurrentActivityTracker,
) : CredentialManagerAdapter {
    override suspend fun chooseGoogleAccount(
        mustEnableAutoSelect: Boolean,
    ): AppResult<String> {
        val activityContext = currentActivityTracker.current() ?: return noActivityError()

        val credentialManager = CredentialManager.create(activityContext)

        val result = getCredential(
            credentialManager,
            activityContext,
            autoSelect = mustEnableAutoSelect,
        )

        if (result is AppResult.Error
            && result.error == ApplicationError.NoCredentialsFound
            && mustEnableAutoSelect
        ) {
            // Auto-select fails when the device has multiple Google accounts; retry with the picker.
            return getCredential(credentialManager, activityContext, autoSelect = false)
        }

        return result
    }

    private suspend fun getCredential(
        credentialManager: CredentialManager,
        activityContext: Activity,
        autoSelect: Boolean,
    ): AppResult<String> {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(autoSelect)
            .setNonce(CryptoUtils.generateNonce())
            .build()

        val credentialRequest: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val idToken = onBoundary(toError = { classifyGetCredentialFailure(it) }) {
            val result = credentialManager.getCredential(
                context = activityContext,
                request = credentialRequest,
            )

            val credential = result.credential
            val isSupportedCredentialType = credential is CustomCredential
                    && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

            if (isSupportedCredentialType) {
                GoogleIdTokenCredential.createFrom(credential.data).idToken
            } else {
                ExceptionLogger.logMasked("Unsupported credential type: ${credential.type}")

                null
            }
        }.let { result ->
            when (result) {
                is AppResult.Error -> return result
                is AppResult.Success -> result.data
            }
        }

        return if (idToken != null) AppResult.Success(data = idToken)
        else AppResult.Error(error = ApplicationError.Unknown)
    }

    private fun classifyGetCredentialFailure(exception: Exception): ApplicationError {
        // O Credential Manager não sinaliza falta de internet com exceção própria: sem conexão,
        // qualquer falha que não seja cancelamento pelo Corretor é atribuída a ela.
        val error = exception.toApplicationError()
        if (error == ApplicationError.UserCancellation) return error

        return if (connectivityAdapter.isDeviceConnectedToInternet()) error else ApplicationError.NoInternet
    }

    override suspend fun clearGoogleCredentialState(): AppResult<Unit> {
        val activityContext = currentActivityTracker.current() ?: return noActivityError()

        val credentialManager = CredentialManager.create(activityContext)
        val clearCredentialStateRequest = ClearCredentialStateRequest()

        return onBoundary(toError = { it.toApplicationError() }) {
            credentialManager.clearCredentialState(clearCredentialStateRequest)
        }
    }

    private fun noActivityError(): AppResult.Error {
        ExceptionLogger.logMasked("Credential Manager chamado sem Activity em primeiro plano")

        return AppResult.Error(error = ApplicationError.Unknown)
    }
}
