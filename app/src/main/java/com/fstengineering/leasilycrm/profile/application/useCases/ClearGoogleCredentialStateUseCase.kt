package com.fstengineering.leasilycrm.profile.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.CredentialManagerAdapter

class ClearGoogleCredentialStateUseCase(
    private val credentialManager: CredentialManagerAdapter,
) {
    /** Esquece a conta Google selecionada, para que o próximo login volte a pedir a escolha da conta. */
    suspend operator fun invoke(): AppResult<Unit> = credentialManager.clearGoogleCredentialState()
}
