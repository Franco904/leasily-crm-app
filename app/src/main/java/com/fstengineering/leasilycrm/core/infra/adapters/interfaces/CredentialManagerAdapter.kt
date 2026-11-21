package com.fstengineering.leasilycrm.core.infra.adapters.interfaces

import android.app.Activity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface CredentialManagerAdapter {
    suspend fun chooseGoogleAccount(
        activityContext: Activity,
        mustEnableAutoSelect: Boolean,
    ): AppResult<String>

    suspend fun clearGoogleCredentialState(
        activityContext: Activity,
    ): AppResult<Unit>
}
