package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError

data class AssistancePreferredFeaturesCount(
    val bedrooms: Int,
    val enSuites: Int,
    val doubleBeds: Int,
    val singleBeds: Int,
    val bathrooms: Int,
    val parkingSpots: Int,
) {
    companion object {
        fun parse(
            bedrooms: Int,
            enSuites: Int,
            doubleBeds: Int,
            singleBeds: Int,
            bathrooms: Int,
            parkingSpots: Int,
        ): AssistancePreferredFeaturesCount {
            validate(
                bedrooms = bedrooms,
                enSuites = enSuites,
                doubleBeds = doubleBeds,
                singleBeds = singleBeds,
                bathrooms = bathrooms,
                parkingSpots = parkingSpots,
            )
                .onError { throw it.asException() }

            return AssistancePreferredFeaturesCount(
                bedrooms = bedrooms,
                enSuites = enSuites,
                doubleBeds = doubleBeds,
                singleBeds = singleBeds,
                bathrooms = bathrooms,
                parkingSpots = parkingSpots,
            )
        }

        fun validate(
            bedrooms: Int,
            enSuites: Int,
            doubleBeds: Int,
            singleBeds: Int,
            bathrooms: Int,
            parkingSpots: Int,
        ): AppResult<Unit, DomainError.Assistance> {
            return when {
                bedrooms < 0 -> AppResult.Error(DomainError.Assistance.BedroomsCountIsNegative)
                enSuites < 0 -> AppResult.Error(DomainError.Assistance.EnSuitesCountIsNegative)
                doubleBeds < 0 -> AppResult.Error(DomainError.Assistance.DoubleBedsCountIsNegative)
                singleBeds < 0 -> AppResult.Error(DomainError.Assistance.SingleBedsCountIsNegative)
                bathrooms < 0 -> AppResult.Error(DomainError.Assistance.BathroomsCountIsNegative)
                parkingSpots < 0 -> AppResult.Error(DomainError.Assistance.ParkingSpotsCountIsNegative)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
