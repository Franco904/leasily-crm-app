package com.fstengineering.leasilycrm.core.infra.adapters.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.InfraError

interface DatabaseAdapter {
    suspend fun transaction(
        transactionBlock: suspend () -> Unit,
    ): AppResult<Unit, InfraError>
}
