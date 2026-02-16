package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.ParsingError

enum class StayStatus(val id: Int) {
    Upcoming(id = 0),
    CheckedIn(id = 1),
    CheckedOut(id = 2);

    val isCheckInDone: Boolean
        get() = this == CheckedIn || this == CheckedOut

    companion object {
        fun parse(id: Int) =
            entries.find { it.id == id } ?: throw ParsingError.Stay.StatusInvalid.asException()
    }
}