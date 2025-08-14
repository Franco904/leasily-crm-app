package com.fstengineering.leasilycrm.core.presentation.external

import android.app.Activity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.fstengineering.leasilycrm.core.data.constants.CLIENT_ID
import com.fstengineering.leasilycrm.core.data.utils.CryptoUtils
import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.presentation.exceptions.CredentialManagerException
import com.fstengineering.leasilycrm.core.presentation.exceptions.asCredentialManagerException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.ktor.utils.io.CancellationException

object AppCredentialManager {
    suspend fun chooseGoogleAccount(
        activityContext: Activity,
        isSignIn: Boolean,
        mustEnableAutoSelect: Boolean,
    ): GoogleUserCredentials {
        val credentialManager = CredentialManager.create(activityContext)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(CLIENT_ID)
            .setFilterByAuthorizedAccounts(isSignIn)
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
                    && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL

            if (!isSupportedCredentialType) {
                throw CredentialManagerException.UnsupportedCrendentialTypeException(
                    type = credential.type,
                )
            }

            // Extracting the ID token to validate and authenticate on the back-end server.
            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(credential.data)

            return GoogleUserCredentials(
                idToken = googleIdTokenCredential.idToken,
                name = googleIdTokenCredential.displayName,
                profilePictureUrl = googleIdTokenCredential.profilePictureUri?.toString(),
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CredentialManagerException || e is CancellationException) throw e

            throw e.asCredentialManagerException()
        }
    }

    suspend fun clearGoogleCredentialState(activityContext: Activity) {
        val credentialManager = CredentialManager.create(activityContext)
        val clearCredentialStateRequest = ClearCredentialStateRequest()

        try {
            credentialManager.clearCredentialState(clearCredentialStateRequest)
        } catch (e: Exception) {
            if (e is CredentialManagerException || e is CancellationException) throw e

            throw e.asCredentialManagerException()
        }
    }

    data class GoogleUserCredentials(
        val idToken: String,
        val name: String? = null,
        val profilePictureUrl: String? = null,
    ) {
        fun toUser() = User(
            name = name,
            profilePictureUrl = profilePictureUrl,
        )
    }
}
