package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class StayPeriod private constructor(
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
) {
    companion object {
        fun parse(
            checkInDateTime: AppDateTime?,
            checkInTime: Pair<Int?, Int?>?,
            checkOutDateTime: AppDateTime?,
            checkOutTime: Pair<Int?, Int?>?,
        ): StayPeriod {
            validate(
                checkInDateTime = checkInDateTime,
                checkInTime = checkInTime,
                checkOutDateTime = checkOutDateTime,
                checkOutTime = checkOutTime,
            )
                .onError { throw it.asException() }

            requireNotNull(checkInDateTime)
            requireNotNull(checkOutDateTime)

            return StayPeriod(
                checkInDateTime = checkInDateTime.withHourMinute(
                    hour = checkInTime?.first!!,
                    minute = checkInTime.second!!,
                ),
                checkOutDateTime = checkOutDateTime.withHourMinute(
                    hour = checkOutTime?.first!!,
                    minute = checkOutTime.second!!,
                ),
            )
        }

        fun validate(
            checkInDateTime: AppDateTime?,
            checkInTime: Pair<Int?, Int?>?,
            checkOutDateTime: AppDateTime?,
            checkOutTime: Pair<Int?, Int?>?,
        ): AppResult<Unit, DomainError.Stay> {
            // propertyStayCheckInDateTimes.isNotEmpty() -> {
            //                val isThereAnyPropertyOverbooking = propertyStayCheckInDateTimes
            //                    .filter { it != checkInDateTimeBeforeEdit }
            //                    .any { existingStayCheckInDateTime ->
            //                        checkInDateTime <= existingStayCheckInDateTime
            //                                && checkOutDateTime >= existingStayCheckInDateTime
            //                    }
            //
            //                if (!isThereAnyPropertyOverbooking) return AppResult.Success(Unit)
            //
            //                return AppResult.Error(ParsingError.StayPeriod.IsOverlappingOtherPropertyStayPeriod)
            //            }

            return when {
                checkInDateTime == null -> AppResult.Error(DomainError.Stay.CheckInDateRequired)
                checkInTime?.first == null || checkInTime.second == null -> AppResult.Error(DomainError.Stay.CheckInTimeRequired)
                checkOutDateTime == null -> AppResult.Error(DomainError.Stay.CheckOutDateRequired)
                checkOutTime?.first == null || checkOutTime.second == null -> AppResult.Error(DomainError.Stay.CheckOutTimeRequired)
                checkInDateTime.isSameDayAs(checkOutDateTime) -> AppResult.Error(DomainError.Stay.PeriodStartAndEndDatesAreTheSame)
                checkInDateTime.isAfterDayOf(checkOutDateTime) -> AppResult.Error(DomainError.Stay.PeriodStartDateIsAfterEndDate)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
