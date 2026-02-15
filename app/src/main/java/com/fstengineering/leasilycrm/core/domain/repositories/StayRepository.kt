package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.helpers.StayCheckStatus
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface StayRepository {
    suspend fun insertOrUpdate(stay: Stay): AppResult<Unit, DataError>

    suspend fun delete(id: Long): AppResult<Unit, DataError>

    suspend fun getById(stayId: Long): AppResult<Stay, DataError>

    fun watchById(stayId: Long): Flow<AppResult<Stay?, DataError>>

    fun watchAll(): Flow<AppResult<ImmutableList<Stay>, DataError>>

    fun watchAllUpcomingCheckInsAndCheckOuts(): Flow<AppResult<ImmutableList<StayCheckStatus>, DataError>>

    fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean, DataError>>

    suspend fun setMustNotShowCheckOutDialogAnymore(
        mustNotShowCheckOutDialogAnymore: Boolean,
    ): AppResult<Unit, DataError>
}
