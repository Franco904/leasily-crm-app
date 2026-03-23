package com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswer
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswersFormField
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyDraftFormUiState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun CharacteristicsCountFormField(
    form: PropertyDraftFormUiState.Loaded,
    onFormChange: (PropertyDraftFormUiState.Loaded) -> Unit,
    modifier: Modifier = Modifier,
) {
    CountAnswersFormField(
        countAnswers = persistentListOf(
            CountAnswer(
                label = stringResource(R.string.property_registration_bedrooms_body_text),
                count = form.bedroomsCount,
                onCountChange = { onFormChange(form.copy(bedroomsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_en_suites_body_text),
                count = form.enSuitesCount,
                onCountChange = { onFormChange(form.copy(enSuitesCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_double_beds_body_text),
                count = form.doubleBedsCount,
                onCountChange = { onFormChange(form.copy(doubleBedsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_single_beds_body_text),
                count = form.singleBedsCount,
                onCountChange = { onFormChange(form.copy(singleBedsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_bathrooms_body_text),
                count = form.bathroomsCount,
                onCountChange = { onFormChange(form.copy(bathroomsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_parking_spots_body_text),
                count = form.parkingSpotsCount,
                onCountChange = { onFormChange(form.copy(parkingSpotsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_air_conditioners_body_text),
                count = form.airConditionersCount,
                onCountChange = { onFormChange(form.copy(airConditionersCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_max_guests_body_text),
                count = form.maxGuestsCount,
                onCountChange = { onFormChange(form.copy(maxGuestsCount = it)) },
            ),
        ),
        modifier = modifier
    )
}
