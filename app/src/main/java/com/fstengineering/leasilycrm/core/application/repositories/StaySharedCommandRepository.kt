package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.models.StayListReadModel
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayCheckStatus
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface StaySharedCommandRepository {
    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>

    fun watchAll(): Flow<AppResult<ImmutableList<StayListReadModel>, ApplicationError>>

    fun watchAllUpcomingCheckInsAndCheckOuts(): Flow<AppResult<ImmutableList<StayCheckStatus>, ApplicationError>>

    fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean, ApplicationError>>

    suspend fun setMustNotShowCheckOutDialogAnymore(
        mustNotShowCheckOutDialogAnymore: Boolean,
    ): AppResult<Unit, ApplicationError>

    suspend fun registerCheckIn(id: Long): AppResult<Unit, ApplicationError>

    suspend fun undoRegisterCheckIn(id: Long): AppResult<Unit, ApplicationError>
}
