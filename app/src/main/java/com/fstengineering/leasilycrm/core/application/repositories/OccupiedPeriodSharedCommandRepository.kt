package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface OccupiedPeriodSharedCommandRepository {
    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>

    suspend fun registerCheckIn(id: Long): AppResult<Unit, ApplicationError>

    suspend fun undoRegisterCheckIn(id: Long): AppResult<Unit, ApplicationError>

    suspend fun registerCheckOut(id: Long): AppResult<Unit, ApplicationError>

    suspend fun undoRegisterCheckOut(id: Long): AppResult<Unit, ApplicationError>

    suspend fun setMustNotShowCheckOutDialogAnymore(
        mustNotShowCheckOutDialogAnymore: Boolean,
    ): AppResult<Unit, ApplicationError>
}
