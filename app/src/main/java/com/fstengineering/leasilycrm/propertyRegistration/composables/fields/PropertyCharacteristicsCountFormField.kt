package com.fstengineering.leasilycrm.propertyRegistration.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.entities.Property
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
                count = propertyForm.bedrooms,
                onCountChange = { onPropertyFormChange(propertyForm.copy(bedrooms = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_en_suites_body_text),
                count = propertyForm.enSuites,
                onCountChange = { onPropertyFormChange(propertyForm.copy(enSuites = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_double_beds_body_text),
                count = propertyForm.doubleBeds,
                onCountChange = { onPropertyFormChange(propertyForm.copy(doubleBeds = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_single_beds_body_text),
                count = propertyForm.singleBeds,
                onCountChange = { onPropertyFormChange(propertyForm.copy(singleBeds = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_bathrooms_body_text),
                count = propertyForm.bathrooms,
                onCountChange = { onPropertyFormChange(propertyForm.copy(bathrooms = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_parking_spots_body_text),
                count = propertyForm.parkingSpots,
                onCountChange = { onPropertyFormChange(propertyForm.copy(parkingSpots = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_air_conditioners_body_text),
                count = propertyForm.airConditioners,
                onCountChange = { onPropertyFormChange(propertyForm.copy(airConditioners = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_max_guests_body_text),
                count = propertyForm.maxGuests,
                onCountChange = { onPropertyFormChange(propertyForm.copy(maxGuests = it)) },
            ),
        ),
        modifier = modifier
    )
}
