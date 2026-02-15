package com.fstengineering.leasilycrm.core.domain.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.models.helpers.StayCheckNotificationStatus
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface GetTodayCheckInsCheckOutsToNotifyUseCase {
    suspend operator fun invoke(): AppResult<List<StayCheckNotificationStatus>, DataError>
}
