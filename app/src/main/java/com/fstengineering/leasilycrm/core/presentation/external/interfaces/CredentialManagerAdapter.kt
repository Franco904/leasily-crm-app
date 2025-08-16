package com.fstengineering.leasilycrm.core.presentation.external.interfaces

import android.app.Activity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.presentation.external.models.GoogleUserCredentials
import com.fstengineering.leasilycrm.core.presentation.utils.CredentialsError

interface CredentialManagerAdapter {
    suspend fun chooseGoogleAccount(
        activityContext: Activity,
        isSignIn: Boolean,
        mustEnableAutoSelect: Boolean,
    ): AppResult<GoogleUserCredentials, CredentialsError>

    suspend fun clearGoogleCredentialState(
        activityContext: Activity,
    ): AppResult<Unit, CredentialsError>
}
