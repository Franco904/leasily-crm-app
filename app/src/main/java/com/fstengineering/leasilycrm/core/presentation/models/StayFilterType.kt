package com.fstengineering.leasilycrm.core.presentation.models

import com.fstengineering.leasilycrm.R

enum class StayFilterType(
    val label: Int,
) {
    UpcomingCheckIn(R.string.stays_upcoming_check_in_filter_text),
    UpcomingCheckOut(R.string.stays_upcoming_check_out_filter_text),
    Finished(R.string.stays_finished_filter_text),
    UnavailablePeriod(R.string.stays_unavailable_period_filter_text),
    All(R.string.stays_all_filter_text),
}
