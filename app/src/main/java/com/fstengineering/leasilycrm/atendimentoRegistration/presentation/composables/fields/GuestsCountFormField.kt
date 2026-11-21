package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswer
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswersFormField
import kotlinx.collections.immutable.persistentListOf

@Composable
fun GuestsCountFormField(
    atendimentoForm: AtendimentoDraftFormUiState.Loaded,
    onAtendimentoFormChange: (AtendimentoDraftFormUiState.Loaded) -> Unit,
    modifier: Modifier = Modifier,
) {
    CountAnswersFormField(
        countAnswers = persistentListOf(
            CountAnswer(
                label = stringResource(R.string.assistance_registration_adult_guests_title),
                count = atendimentoForm.adultsCount,
                onCountChange = { onAtendimentoFormChange(atendimentoForm.copy(adultsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.assistance_registration_kid_guests_title),
                count = atendimentoForm.kidsCount,
                onCountChange = { onAtendimentoFormChange(atendimentoForm.copy(kidsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.assistance_registration_baby_guests_title),
                count = atendimentoForm.babiesCount,
                onCountChange = { onAtendimentoFormChange(atendimentoForm.copy(babiesCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.assistance_registration_animal_guests_title),
                count = atendimentoForm.animalsCount,
                onCountChange = { onAtendimentoFormChange(atendimentoForm.copy(animalsCount = it)) },
            ),
        ),
        modifier = modifier
    )
}
