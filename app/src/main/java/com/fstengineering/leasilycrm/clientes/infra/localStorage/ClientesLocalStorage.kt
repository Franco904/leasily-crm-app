package com.fstengineering.leasilycrm.clientes.infra.localStorage

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import com.fstengineering.leasilycrm.core.infra.localStorage.plain.PlainPreferences
import kotlinx.coroutines.flow.firstOrNull

/** Preferência de ordenação das listagens de Leads e de Hóspedes. */
class ClientesLocalStorage(
    private val plainLocalStorage: LocalStorage<PlainPreferences>,
) {
    /** Id da ordenação preferida na listagem de Leads; 1 (por nome) se nunca escolhida. */
    suspend fun getLeadsOrderTypeId(): AppResult<Int> =
        readOrderTypeId { it.leadsOrderTypeId }

    /** Id da ordenação preferida na listagem de Hóspedes; 1 (por nome) se nunca escolhida. */
    suspend fun getHospedesOrderTypeId(): AppResult<Int> =
        readOrderTypeId { it.tenantsOrderTypeId }

    suspend fun saveLeadsOrderTypeId(orderTypeId: Int): AppResult<Unit> =
        plainLocalStorage.savePreferences { it.copy(leadsOrderTypeId = orderTypeId) }

    suspend fun saveHospedesOrderTypeId(orderTypeId: Int): AppResult<Unit> =
        plainLocalStorage.savePreferences { it.copy(tenantsOrderTypeId = orderTypeId) }

    private suspend fun readOrderTypeId(
        selector: (PlainPreferences) -> Int?,
    ): AppResult<Int> =
        plainLocalStorage.readPreferences().fold(
            onError = { AppResult.Error(error = it) },
            onSuccess = { prefsFlow ->
                AppResult.Success(
                    data = prefsFlow.firstOrNull()?.let(selector) ?: DEFAULT_ORDER_TYPE_ID
                )
            },
        )

    private companion object {
        const val DEFAULT_ORDER_TYPE_ID = 1
    }
}
