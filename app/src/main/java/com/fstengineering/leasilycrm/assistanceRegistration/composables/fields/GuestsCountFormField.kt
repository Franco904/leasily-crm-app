package com.fstengineering.leasilycrm.assistanceRegistration.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswer
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswersFormField
import kotlinx.collections.immutable.persistentListOf

@Composable
fun GuestsCountFormField(
    assistanceForm: Assistance,
    onAssistanceFormChange: (Assistance) -> Unit,
    modifier: Modifier = Modifier,
) {
    CountAnswersFormField(
        countAnswers = persistentListOf(
            CountAnswer(
                label = stringResource(R.string.assistance_registration_adult_guests_title),
                count = assistanceForm.qtAdultGuests,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(qtAdultGuests = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.assistance_registration_kid_guests_title),
                count = assistanceForm.qtKidGuests,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(qtKidGuests = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.assistance_registration_baby_guests_title),
                count = assistanceForm.qtBabyGuests,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(qtBabyGuests = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.assistance_registration_animal_guests_title),
                count = assistanceForm.qtAnimalGuests,
                onCountChange = { onAssistanceFormChange(assistanceForm.copy(qtAnimalGuests = it)) },
            ),
        ),
        modifier = modifier
    )
}
