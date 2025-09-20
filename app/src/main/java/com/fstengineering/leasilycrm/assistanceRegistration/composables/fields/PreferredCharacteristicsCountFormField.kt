package com.fstengineering.leasilycrm.assistanceRegistration.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswer
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswersFormField
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PreferredCharacteristicsCountFormField(
    assistanceForm: Assistance,
    onAssistanceFormChange: (Assistance) -> Unit,
    modifier: Modifier = Modifier,
) {
    CountAnswersFormField(
        countAnswers = persistentListOf(
            CountAnswer(
                label = stringResource(R.string.property_registration_bedrooms_body_text),
                count = assistanceForm.qtBedrooms,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(qtBedrooms = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_en_suites_body_text),
                count = assistanceForm.qtEnSuites,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(qtEnSuites = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_double_beds_body_text),
                count = assistanceForm.qtDoubleBeds,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(qtDoubleBeds = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_single_beds_body_text),
                count = assistanceForm.qtSingleBeds,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(qtSingleBeds = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_bathrooms_body_text),
                count = assistanceForm.qtBathrooms,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(qtBathrooms = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_parking_spots_body_text),
                count = assistanceForm.qtParkingSpots,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(qtParkingSpots = it)) },
            ),
        ),
        modifier = modifier
    )
}
