package com.fstengineering.leasilycrm.stayDetails.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface OccupiedPeriodCommandRepository {
    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>

    suspend fun registerCheckIn(id: Long): AppResult<Unit, ApplicationError>

    suspend fun undoRegisterCheckIn(id: Long): AppResult<Unit, ApplicationError>

    suspend fun registerCheckOut(id: Long): AppResult<Unit, ApplicationError>

    suspend fun undoRegisterCheckOut(id: Long): AppResult<Unit, ApplicationError>

    suspend fun setMustNotShowCheckOutDialogAnymore(
        mustNotShowCheckOutDialogAnymore: Boolean,
    ): AppResult<Unit, ApplicationError>
}
