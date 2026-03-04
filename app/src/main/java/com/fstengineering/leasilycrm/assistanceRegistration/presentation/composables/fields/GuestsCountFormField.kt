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
fun GuestsCountFormField(
    assistanceForm: AssistanceDraftFormUiState.Loaded,
    onAssistanceFormChange: (AssistanceDraftFormUiState.Loaded) -> Unit,
    modifier: Modifier = Modifier,
) {
    CountAnswersFormField(
        countAnswers = persistentListOf(
            CountAnswer(
                label = stringResource(R.string.assistance_registration_adult_guests_title),
                count = assistanceForm.adultsCount,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(adultsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.assistance_registration_kid_guests_title),
                count = assistanceForm.kidsCount,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(kidsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.assistance_registration_baby_guests_title),
                count = assistanceForm.babiesCount,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(babiesCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.assistance_registration_animal_guests_title),
                count = assistanceForm.animalsCount,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(animalsCount = it)) },
            ),
        ),
        modifier = modifier
    )
}
