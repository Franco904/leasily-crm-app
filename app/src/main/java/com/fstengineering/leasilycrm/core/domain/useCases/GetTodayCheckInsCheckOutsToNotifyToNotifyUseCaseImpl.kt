package com.fstengineering.leasilycrm.core.domain.useCases

import android.util.Log
import com.fstengineering.leasilycrm.core.domain.models.StayCheckNotificationStatus
import com.fstengineering.leasilycrm.core.domain.repositories.SettingsRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.GetTodayCheckInsCheckOutsToNotifyUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.first

class GetTodayCheckInsCheckOutsToNotifyToNotifyUseCaseImpl(
    private val stayRepository: StayRepository,
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
) : GetTodayCheckInsCheckOutsToNotifyUseCase {
    override suspend fun invoke(): AppResult<List<StayCheckNotificationStatus>, DataSourceError> {
        val isTodayCheckInCheckOutNotificationEnabled = settingsRepository.watchSettings().first()
            .fold(onError = { false }, onSuccess = { it.isTodayCheckInCheckOutNotificationEnabled })

        val hasSessionExpired = userRepository.watchSessionExpiration().first()
            .fold(onError = { true }, onSuccess = { it })

        Log.d(
            "GetTodayCheckInsCheckOutsToNotifyUseCase",
            "isTodayCheckInCheckOutNotificationEnabled: $isTodayCheckInCheckOutNotificationEnabled"
        )
        Log.d("GetTodayCheckInsCheckOutsToNotifyUseCase", "hasSessionExpired: $hasSessionExpired")

        if (!isTodayCheckInCheckOutNotificationEnabled || hasSessionExpired) {
            return AppResult.Success(data = emptyList())
        }

        return stayRepository.watchAllUpcomingCheckInsAndCheckOuts().first()
            .fold(
                onError = { error -> AppResult.Success(data = persistentListOf()) },
                onSuccess = { upcomingCheckInsAndCheckOuts ->
                    val todayCheckInsAndCheckOutsToNotify = upcomingCheckInsAndCheckOuts
                        .map {
                            val mustNotifyCheckIn =
                                !it.isCheckInDone && it.checkInDateTime.isToday()
                            val mustNotifyCheckOut = it.checkOutDateTime.isToday()

                            StayCheckNotificationStatus(
                                tenantName = it.tenantName,
                                propertyTypeName = it.propertyTypeName,
                                propertyOwnerName = it.propertyOwnerName,
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
