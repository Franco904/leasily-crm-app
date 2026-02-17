package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class AssistanceGuestsCount private constructor(
    val adults: Int,
    val kids: Int,
    val babies: Int,
    val animals: Int,
) {
    val totalGuests get() = adults + kids + babies + animals

    companion object {
        fun parse(adults: Int, kids: Int, babies: Int, animals: Int): AssistanceGuestsCount {
            validate(adults, kids, babies, animals)
                .onError { throw it.asException() }

            return AssistanceGuestsCount(adults, kids, babies, animals)
        }

        fun validate(
            adults: Int,
            kids: Int,
            babies: Int,
            animals: Int,
        ): AppResult<Unit, ParsingError.Assistance> {
            return when {
                adults < 0 -> AppResult.Error(ParsingError.Assistance.AdultGuestsCountIsNegative)
                kids < 0 -> AppResult.Error(ParsingError.Assistance.KidsGuestsCountIsNegative)
                babies < 0 -> AppResult.Error(ParsingError.Assistance.BabiesGuestsCountIsNegative)
                animals < 0 -> AppResult.Error(ParsingError.Assistance.AnimalsGuestsCountIsNegative)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
