package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.helpers.StayCheckStatus
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface StayRepository {
    suspend fun insertOrUpdate(stay: Stay): AppResult<Unit, ApplicationError>

    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>

    suspend fun getById(stayId: Long): AppResult<Stay, ApplicationError>

    fun watchById(stayId: Long): Flow<AppResult<Stay?, ApplicationError>>

    fun watchAll(): Flow<AppResult<ImmutableList<Stay>, ApplicationError>>

    fun watchAllUpcomingCheckInsAndCheckOuts(): Flow<AppResult<ImmutableList<StayCheckStatus>, ApplicationError>>

    fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean, ApplicationError>>

    suspend fun setMustNotShowCheckOutDialogAnymore(
        mustNotShowCheckOutDialogAnymore: Boolean,
    ): AppResult<Unit, ApplicationError>
}
