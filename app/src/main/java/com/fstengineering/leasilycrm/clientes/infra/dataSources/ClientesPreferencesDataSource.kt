package com.fstengineering.leasilycrm.clientes.infra.dataSources

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import kotlinx.coroutines.flow.firstOrNull

/** Preferência de ordenação das listagens de Leads e de Hóspedes. */
class ClientesPreferencesDataSource(
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) {
    /** Id da ordenação preferida na listagem de Leads; 1 (por nome) se nunca escolhida. */
    suspend fun getLeadsOrderTypeId(): AppResult<Int> =
        readOrderTypeId { it.leadsOrderTypeId }

    /** Id da ordenação preferida na listagem de Hóspedes; 1 (por nome) se nunca escolhida. */
    suspend fun getHospedesOrderTypeId(): AppResult<Int> =
        readOrderTypeId { it.tenantsOrderTypeId }

    suspend fun saveLeadsOrderTypeId(orderTypeId: Int): AppResult<Unit> =
        plainPreferencesDataSource.savePreferences { it.copy(leadsOrderTypeId = orderTypeId) }

    suspend fun saveHospedesOrderTypeId(orderTypeId: Int): AppResult<Unit> =
        plainPreferencesDataSource.savePreferences { it.copy(tenantsOrderTypeId = orderTypeId) }

    private suspend fun readOrderTypeId(
        selector: (PlainPreferences) -> Int?,
    ): AppResult<Int> =
        plainPreferencesDataSource.readPreferences().fold(
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
