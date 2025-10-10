package com.fstengineering.leasilycrm.core.domain.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.models.StayCheckNotificationStatus
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

interface GetTodayCheckInsCheckOutsToNotifyUseCase {
    suspend operator fun invoke(): AppResult<List<StayCheckNotificationStatus>, DataSourceError>
}
