package com.fstengineering.leasilycrm.core.data.adapters.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface DatabaseAdapter {
    suspend fun withTransaction(
        transactionBlock: suspend () -> Unit,
    ): AppResult<Unit, DataError>
}
