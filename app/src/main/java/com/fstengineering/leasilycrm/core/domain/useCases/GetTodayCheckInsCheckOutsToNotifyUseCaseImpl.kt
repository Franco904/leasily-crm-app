package com.fstengineering.leasilycrm.core.domain.useCases

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.StayCheckNotificationStatus
import com.fstengineering.leasilycrm.core.domain.repositories.AuthRepository
import com.fstengineering.leasilycrm.core.domain.repositories.SettingsRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.GetTodayCheckInsCheckOutsToNotifyUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class GetTodayCheckInsCheckOutsToNotifyUseCaseImpl(
    private val authRepository: AuthRepository,
    private val stayRepository: StayRepository,
    private val settingsRepository: SettingsRepository,
    private val now: AppDateTime,
) : GetTodayCheckInsCheckOutsToNotifyUseCase {
    override suspend fun invoke(): AppResult<List<StayCheckNotificationStatus>, DataError> {
        val isTodayCheckInCheckOutNotificationEnabled =
            settingsRepository.watchSettings().firstOrNull()
                ?.fold(
                    onError = { false },
                    onSuccess = { it.isTodayCheckInCheckOutNotificationEnabled }
                ) ?: false

        val isSessionAlive = authRepository.isSessionAlive()
            .fold(onError = { false }, onSuccess = { it })

        if (now.dateTime().hour > 9 || !isTodayCheckInCheckOutNotificationEnabled || !isSessionAlive) {
            return AppResult.Success(data = emptyList())
        }

        return stayRepository.watchAllUpcomingCheckInsAndCheckOuts().first()
            .fold(
                onError = { error -> AppResult.Success(data = persistentListOf()) },
                onSuccess = { upcomingCheckInsAndCheckOuts ->
                    val todayCheckInsAndCheckOutsToNotify = upcomingCheckInsAndCheckOuts
                        .filter {
                            it.tenantName != null
                                    && it.propertyTypeId != null
                                    && it.propertyOwnerName != null
                        }
                        .map {
                            val mustNotifyCheckIn =
                                !it.isCheckInDone && it.checkInDateTime.isToday()
                            val mustNotifyCheckOut = it.checkOutDateTime.isToday()

                            StayCheckNotificationStatus(
                                tenantName = requireNotNull(it.tenantName),
                                propertyTypeId = requireNotNull(it.propertyTypeId),
                                propertyOwnerName = requireNotNull(it.propertyOwnerName),
                                hourMinute = if (mustNotifyCheckIn) {
                                    it.checkInDateTime.toHourMinute()
                                } else it.checkOutDateTime.toHourMinute(),
                                mustNotifyCheckIn = mustNotifyCheckIn,
                                mustNotifyCheckOut = mustNotifyCheckOut,
                            )
                        }
                        .filter { it.mustNotifyCheckIn || it.mustNotifyCheckOut }

                    AppResult.Success(data = todayCheckInsAndCheckOutsToNotify)
                },
            )
    }
}
