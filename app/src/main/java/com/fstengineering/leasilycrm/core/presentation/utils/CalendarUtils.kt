package com.fstengineering.leasilycrm.core.presentation.utils

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.models.AppDate
import com.fstengineering.leasilycrm.core.application.models.toDate
import kotlinx.collections.immutable.ImmutableSet
import java.util.Calendar

val daysOfWeek = listOf(
    R.string.sunday_label,
    R.string.monday_label,
    R.string.tuesday_label,
    R.string.wednesday_label,
    R.string.thursday_label,
    R.string.friday_label,
    R.string.saturday_label,
)

private val months = mapOf(
    1 to R.string.january_label,
    2 to R.string.february_label,
    3 to R.string.march_label,
    4 to R.string.april_label,
    5 to R.string.may_label,
    6 to R.string.june_label,
    7 to R.string.july_label,
    8 to R.string.august_label,
    9 to R.string.september_label,
    10 to R.string.october_label,
    11 to R.string.november_label,
    12 to R.string.december_label,
)

fun Context.getMonthLabelByNumber(monthNumber: Int): String {
    return getString(months[monthNumber] ?: R.string.january_label)
}

fun Context.getMonthLabelAbbreviationByNumber(monthNumber: Int): String {
    return getString(months[monthNumber] ?: R.string.january_label).substring(0..2)
}

fun getMonthTimestamp(month: Int, year: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1)

    return calendar.timeInMillis
}

fun getNumberOfDaysOfMonth(month: Int, year: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1)

    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun getFirstDayOfWeekOfMonth(month: Int, year: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1)

    return calendar.get(Calendar.DAY_OF_WEEK)
}

@OptIn(ExperimentalMaterial3Api::class)
fun getSelectableDates(
    initialDayOfMonth: Int,
    initialMonth: Int,
    initialYear: Int,
    forceBelow: Boolean = false,
    alreadySelectedDates: ImmutableSet<AppDate>,
): SelectableDates {
    val initialDate = AppDate.of(initialYear, initialMonth, initialDayOfMonth)

    return object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val date = utcTimeMillis.toDate()

            val minMaxCondition =
                if (forceBelow) date <= initialDate
                else date >= initialDate

            return minMaxCondition && date !in alreadySelectedDates
        }

        override fun isSelectableYear(year: Int): Boolean {
            return if (forceBelow) year <= initialYear
            else year >= initialYear
        }
    }
}
