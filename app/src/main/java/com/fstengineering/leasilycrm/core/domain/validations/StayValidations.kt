package com.fstengineering.leasilycrm.core.domain.validations

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError

fun validateStayProperty(propertyId: Long?): AppResult<Unit, ParsingError.Stay> {
    return when {
        propertyId == null -> AppResult.Error(ParsingError.Stay.PropertyRequired)
        else -> AppResult.Success(Unit)
    }
}

fun validateStayTenant(tenantId: Long?): AppResult<Unit, ParsingError.Stay> {
    return when {
        tenantId == null -> AppResult.Error(ParsingError.Stay.PropertyRequired)
        else -> AppResult.Success(Unit)
    }
}

fun validateStayGuestsCount(guestsCount: Int?): AppResult<Unit, ParsingError.Stay> {
    return when {
        guestsCount == null -> AppResult.Error(ParsingError.Stay.GuestsCountRequired)
        guestsCount < 0 -> AppResult.Error(ParsingError.Stay.GuestsCountIsNegative)
        else -> AppResult.Success(Unit)
    }
}

fun validateStayPeriod(
    checkInDateTime: AppDateTime?,
    checkInTime: Pair<Int?, Int?>?,
    checkOutDateTime: AppDateTime?,
    checkOutTime: Pair<Int?, Int?>?,
): AppResult<Unit, ParsingError.Stay> {
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
        checkInDateTime == null -> AppResult.Error(ParsingError.Stay.CheckInDateRequired)
        checkInTime?.first == null || checkInTime.second == null -> AppResult.Error(ParsingError.Stay.CheckInTimeRequired)
        checkOutDateTime == null -> AppResult.Error(ParsingError.Stay.CheckOutDateRequired)
        checkOutTime?.first == null || checkOutTime.second == null -> AppResult.Error(ParsingError.Stay.CheckOutTimeRequired)
        checkInDateTime.isSameDayAs(checkOutDateTime) -> AppResult.Error(ParsingError.Stay.PeriodStartAndEndDatesAreTheSame)
        checkInDateTime.isAfterDayOf(checkOutDateTime) -> AppResult.Error(ParsingError.Stay.PeriodStartDateIsAfterEndDate)
        else -> AppResult.Success(Unit)
    }
}

fun validateStayPrice(totalPrice: AppDecimal): AppResult<Unit, ParsingError.Stay> {
    return when {
        totalPrice.isNegative -> AppResult.Error(ParsingError.Stay.TotalPriceIsNegative)
        else -> AppResult.Success(Unit)
    }
}

fun validateStayCommission(commission: AppDecimal?): AppResult<Unit, ParsingError.Stay> {
    return when {
        commission == null -> AppResult.Error(ParsingError.Stay.CommissionRequired)
        commission.isNegative -> AppResult.Error(ParsingError.Stay.CommissionIsNegative)
        else -> AppResult.Success(Unit)
    }
}

fun validateStayTransferDate(transferDate: AppDate?): AppResult<Unit, ParsingError.Stay> {
    return when {
        transferDate == null -> AppResult.Error(ParsingError.Stay.TransferDateRequired)
        else -> AppResult.Success(Unit)
    }
}

fun validateStayTransferAmount(amount: AppDecimal?): AppResult<Unit, ParsingError.Stay> {
    return when {
        amount == null -> AppResult.Error(ParsingError.Stay.TransferAmountRequired)
        amount.isNegative -> AppResult.Error(ParsingError.Stay.TransferAmountIsNegative)
        else -> AppResult.Success(Unit)
    }
}
