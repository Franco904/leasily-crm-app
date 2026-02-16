package com.fstengineering.leasilycrm.core.domain.validations

import com.fstengineering.leasilycrm.core.domain.constants.EMAIL_REGEX
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType

fun validateName(name: String?): AppResult<Unit, ParsingError> {
    return when {
        name.isNullOrBlank() -> AppResult.Error(ParsingError.PropertyOwner.NameRequired)
        name.length > 100 -> AppResult.Error(ParsingError.PropertyOwner.NameLengthIsGreaterThan100Chars)
        else -> AppResult.Success(Unit)
    }
}

fun validatePhone(phone: String): AppResult<Unit, ParsingError> {
    return when {
        phone.length > 20 -> AppResult.Error(ParsingError.PropertyOwner.PhoneLengthIsGreaterThan20Chars)
        else -> AppResult.Success(Unit)
    }
}

fun validateEmail(email: String): AppResult<Unit, ParsingError> {
    return when {
        !email.trim().matches(Regex(EMAIL_REGEX, RegexOption.IGNORE_CASE)) -> {
            AppResult.Error(ParsingError.PropertyOwner.EmailInvalidFormat)
        }

        email.length > 20 -> AppResult.Error(ParsingError.PropertyOwner.EmailLengthIsGreaterThan120Chars)
        else -> AppResult.Success(Unit)
    }
}

fun validatePropertySeaProximityType(
    id: Long,
    distanceToSea: AppDecimal?,
): AppResult<Unit, ParsingError> {
    val noRulesValidTypes = listOf(
        PropertySeaProximityType.SEA_ACCESS_ID,
        PropertySeaProximityType.SEA_VIEW_ID,
        PropertySeaProximityType.FAR_FROM_SEA_ID,
    )

    return when (id) {
        in noRulesValidTypes -> AppResult.Success(Unit)
        PropertySeaProximityType.DISTANCE_TO_SEA_ID -> validateDistanceToSea(distanceToSea)
        else -> AppResult.Error(ParsingError.PropertySeaProximity.PropertySeaProximityInvalid)
    }
}

fun validateDistanceToSea(
    distance: AppDecimal?,
): AppResult<Unit, ParsingError> {
    return when {
        distance == null -> AppResult.Error(ParsingError.PropertySeaProximity.DistanceToSeaRequired)
        distance.isNegative -> AppResult.Error(ParsingError.PropertySeaProximity.DistanceToSeaIsNegative)
        else -> AppResult.Success(Unit)
    }
}
