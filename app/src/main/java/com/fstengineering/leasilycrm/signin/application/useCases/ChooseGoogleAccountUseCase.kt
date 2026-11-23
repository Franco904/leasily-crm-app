package com.fstengineering.leasilycrm.signin.application.useCases

import android.app.Activity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.CredentialManagerAdapter

class ChooseGoogleAccountUseCase(
    private val credentialManager: CredentialManagerAdapter,
) {
    /** Id token da conta Google escolhida pelo Corretor na tela do sistema, aberta sobre [activityContext]. */
    suspend operator fun invoke(
        activityContext: Activity,
        mustEnableAutoSelect: Boolean,
    ): AppResult<String> = credentialManager.chooseGoogleAccount(
        activityContext = activityContext,
        mustEnableAutoSelect = mustEnableAutoSelect,
    )
}
