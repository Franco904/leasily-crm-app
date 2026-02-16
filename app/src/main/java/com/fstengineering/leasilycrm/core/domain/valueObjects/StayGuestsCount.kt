package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validations.validateStayGuestsCount

class StayGuestsCount private constructor(val count: Int) {
    companion object {
        fun parse(count: Int?): StayGuestsCount {
            validateStayGuestsCount(count)
                .onError { throw it.asException() }

            return StayGuestsCount(requireNotNull(count))
        }
    }
}
