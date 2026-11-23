package com.fstengineering.leasilycrm.signin.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.CredentialManagerAdapter

class ChooseGoogleAccountUseCase(
    private val credentialManager: CredentialManagerAdapter,
) {
    /** Id token da conta Google escolhida pelo Corretor na tela do sistema. */
    suspend operator fun invoke(mustEnableAutoSelect: Boolean): AppResult<String> =
        credentialManager.chooseGoogleAccount(mustEnableAutoSelect = mustEnableAutoSelect)
}
