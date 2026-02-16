package com.fstengineering.leasilycrm.core.domain.validations

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError

fun validateAssistanceSearchedPeriod(
    startDate: AppDate?,
    endDate: AppDate?,
): AppResult<Unit, ParsingError.Assistance> {
    return when {
        startDate == null || endDate == null -> AppResult.Error(ParsingError.Assistance.SearchingPeriodRequired)
        startDate == endDate -> AppResult.Error(ParsingError.Assistance.SearchingPeriodStartAndEndDatesAreTheSame)
        startDate > endDate -> AppResult.Error(ParsingError.Assistance.SearchingPeriodStartDateIsAfterEndDate)
        else -> AppResult.Success(Unit)
    }
}

fun validateAssistanceEstimatedGuestsCount(
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

fun validateAssistancePreferredFeaturesCount(
    bedrooms: Int,
    enSuites: Int,
    doubleBeds: Int,
    singleBeds: Int,
    bathrooms: Int,
    parkingSpots: Int,
): AppResult<Unit, ParsingError.Assistance> {
    return when {
        bedrooms < 0 -> AppResult.Error(ParsingError.Assistance.BedroomsCountIsNegative)
        enSuites < 0 -> AppResult.Error(ParsingError.Assistance.EnSuitesCountIsNegative)
        doubleBeds < 0 -> AppResult.Error(ParsingError.Assistance.DoubleBedsCountIsNegative)
        singleBeds < 0 -> AppResult.Error(ParsingError.Assistance.SingleBedsCountIsNegative)
        bathrooms < 0 -> AppResult.Error(ParsingError.Assistance.BathroomsCountIsNegative)
        parkingSpots < 0 -> AppResult.Error(ParsingError.Assistance.ParkingSpotsCountIsNegative)
        else -> AppResult.Success(Unit)
    }
}

fun validateAssistanceSearchedNeighborhood(
    neighborhood: String?,
): AppResult<Unit, ParsingError.Assistance> {
    return when {
        neighborhood.isNullOrBlank() -> AppResult.Error(ParsingError.Assistance.SearchedNeighborhoodNameRequired)
        else -> AppResult.Success(Unit)
    }
}

fun validateAssistanceInteractionDate(
    interactionDate: AppDate?,
): AppResult<Unit, ParsingError.Assistance> {
    return when {
        interactionDate == null -> AppResult.Error(ParsingError.Assistance.InteractionDateRequired)
        else -> AppResult.Success(Unit)
    }
}

fun validateAssistanceInteractionTitle(
    title: String?,
): AppResult<Unit, ParsingError.Assistance> {
    return when {
        title.isNullOrBlank() -> AppResult.Error(ParsingError.Assistance.InteractionTitleRequired)
        else -> AppResult.Success(Unit)
    }
}
