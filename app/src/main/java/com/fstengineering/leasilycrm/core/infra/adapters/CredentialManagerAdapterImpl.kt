package com.fstengineering.leasilycrm.core.infra.adapters

import android.app.Activity
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.CredentialManagerAdapter
import com.fstengineering.leasilycrm.core.infra.utils.CredentialsError
import com.fstengineering.leasilycrm.core.infra.utils.CryptoUtils
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toCredentialsError
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.ktor.utils.io.CancellationException

class CredentialManagerAdapterImpl : CredentialManagerAdapter {
    override suspend fun chooseGoogleAccount(
        activityContext: Activity,
        mustEnableAutoSelect: Boolean,
    ): AppResult<String, CredentialsError> {
        val credentialManager = CredentialManager.Companion.create(activityContext)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(mustEnableAutoSelect)
            .setNonce(CryptoUtils.generateNonce())
            .build()

        val credentialRequest: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(
                context = activityContext,
                request = credentialRequest,
            )

            val credential = result.credential
            val isSupportedCredentialType = credential is CustomCredential
                    && credential.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

            if (!isSupportedCredentialType) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Unsupported credential type: ${credential.type}")

                return AppResult.Error(error = CredentialsError.UnknownError)
            }

            // Extracting the ID token to validate and authenticate on the back-end server.
            val googleIdTokenCredential = GoogleIdTokenCredential.Companion
                .createFrom(credential.data)

            return AppResult.Success(data = googleIdTokenCredential.idToken)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            return AppResult.Error(error = e.toCredentialsError())
        }
    }

    override suspend fun clearGoogleCredentialState(
        activityContext: Activity,
    ): AppResult<Unit, CredentialsError> {
        val credentialManager = CredentialManager.Companion.create(activityContext)
        val clearCredentialStateRequest = ClearCredentialStateRequest()

        try {
            credentialManager.clearCredentialState(clearCredentialStateRequest)

            return AppResult.Success(data = Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (BuildConfig.DEBUG) e.printStackTrace()

            return AppResult.Error(error = e.toCredentialsError())
        }
    }

    private companion object {
        const val TAG = "AppCredentialManagerImpl"
    }
}
