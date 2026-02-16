package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validations.validateStayPeriod

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
            validateStayPeriod(
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
    }
}
