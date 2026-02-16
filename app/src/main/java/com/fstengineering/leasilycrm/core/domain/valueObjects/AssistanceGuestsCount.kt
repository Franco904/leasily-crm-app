package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validations.validateAssistanceEstimatedGuestsCount

class AssistanceGuestsCount private constructor(
    val adults: Int,
    val kids: Int,
    val babies: Int,
    val animals: Int,
) {
    val totalGuests get() = adults + kids + babies + animals

    companion object {
        fun parse(adults: Int, kids: Int, babies: Int, animals: Int): AssistanceGuestsCount {
            validateAssistanceEstimatedGuestsCount(adults, kids, babies, animals)
                .onError { throw it.asException() }

            return AssistanceGuestsCount(adults, kids, babies, animals)
        }
    }
}
