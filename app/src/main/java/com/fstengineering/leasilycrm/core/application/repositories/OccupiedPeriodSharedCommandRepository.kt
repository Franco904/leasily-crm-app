package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface OccupiedPeriodSharedCommandRepository {
    suspend fun delete(id: Long): AppResult<Unit>

    suspend fun registerCheckIn(id: Long): AppResult<Unit>

    suspend fun undoRegisterCheckIn(id: Long): AppResult<Unit>

    suspend fun registerCheckOut(id: Long): AppResult<Unit>

    suspend fun undoRegisterCheckOut(id: Long): AppResult<Unit>

    suspend fun setMustNotShowCheckOutDialogAnymore(
        mustNotShowCheckOutDialogAnymore: Boolean,
    ): AppResult<Unit>
}
