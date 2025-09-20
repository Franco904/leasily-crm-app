package com.fstengineering.leasilycrm.propertyRegistration.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswer
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswersFormField
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PropertyCharacteristicsCountFormField(
    propertyForm: Property,
    onPropertyFormChange: (Property) -> Unit,
    modifier: Modifier = Modifier,
) {
    CountAnswersFormField(
        countAnswers = persistentListOf(
            CountAnswer(
                label = stringResource(R.string.property_registration_bedrooms_body_text),
                count = propertyForm.qtBedrooms,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtBedrooms = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_en_suites_body_text),
                count = propertyForm.qtEnSuites,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtEnSuites = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_double_beds_body_text),
                count = propertyForm.qtDoubleBeds,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtDoubleBeds = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_single_beds_body_text),
                count = propertyForm.qtSingleBeds,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtSingleBeds = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_bathrooms_body_text),
                count = propertyForm.qtBathrooms,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtBathrooms = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_parking_spots_body_text),
                count = propertyForm.qtParkingSpots,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtParkingSpots = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_air_conditioners_body_text),
                count = propertyForm.qtAirConditioners,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtAirConditioners = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_max_guests_body_text),
                count = propertyForm.qtMaxGuests,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtMaxGuests = it)) },
            ),
        ),
        modifier = modifier
    )
}
