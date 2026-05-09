package com.fstengineering.leasilycrm.core.infra.adapters.interfaces

import android.app.Activity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.CredentialsError

interface CredentialManagerAdapter {
    suspend fun chooseGoogleAccount(
        activityContext: Activity,
        mustEnableAutoSelect: Boolean,
    ): AppResult<String, CredentialsError>

    suspend fun clearGoogleCredentialState(
        activityContext: Activity,
    ): AppResult<Unit, CredentialsError>
}
