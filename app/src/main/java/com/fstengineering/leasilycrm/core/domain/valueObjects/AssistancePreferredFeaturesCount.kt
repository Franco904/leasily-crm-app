package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validations.validateAssistancePreferredFeaturesCount

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
            validateAssistancePreferredFeaturesCount(
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
    }
}
