package com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.core.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswer
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswersFormField
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PreferredCharacteristicsCountFormField(
    assistanceForm: AssistanceDraftFormUiState.Loaded,
    onAssistanceFormChange: (AssistanceDraftFormUiState.Loaded) -> Unit,
    modifier: Modifier = Modifier,
) {
    CountAnswersFormField(
        countAnswers = persistentListOf(
            CountAnswer(
                label = stringResource(R.string.property_registration_bedrooms_body_text),
                count = assistanceForm.bedroomsCount,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(bedroomsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_en_suites_body_text),
                count = assistanceForm.enSuitesCount,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(enSuitesCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_double_beds_body_text),
                count = assistanceForm.doubleBedsCount,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(doubleBedsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_single_beds_body_text),
                count = assistanceForm.singleBedsCount,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(singleBedsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_bathrooms_body_text),
                count = assistanceForm.bathroomsCount,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(bathroomsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_parking_spots_body_text),
                count = assistanceForm.parkingSpotsCount,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(parkingSpotsCount = it)) },
            ),
        ),
        modifier = modifier
    )
}
