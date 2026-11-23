package com.fstengineering.leasilycrm.core.infra.adapters.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface CredentialManagerAdapter {
    /** Id token da conta escolhida. `ApplicationError.Unknown` quando o app não tem tela em primeiro plano. */
    suspend fun chooseGoogleAccount(
        mustEnableAutoSelect: Boolean,
    ): AppResult<String>

    /** Esquece a conta selecionada. `ApplicationError.Unknown` quando o app não tem tela em primeiro plano. */
    suspend fun clearGoogleCredentialState(): AppResult<Unit>
}
