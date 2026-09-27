package com.fstengineering.leasilycrm.occupiedPeriods.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.occupiedPeriods.application.models.dtos.OccupiedPeriodListReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface OccupiedPeriodQueryRepository {
    fun watchAll(): Flow<AppResult<ImmutableList<OccupiedPeriodListReadModel>, ApplicationError>>

    fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean, ApplicationError>>
}
