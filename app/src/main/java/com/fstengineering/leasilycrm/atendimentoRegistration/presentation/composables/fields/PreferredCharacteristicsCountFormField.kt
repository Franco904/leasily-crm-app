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
fun PreferredCharacteristicsCountFormField(
    atendimentoForm: AtendimentoDraftFormUiState.Loaded,
    onAtendimentoFormChange: (AtendimentoDraftFormUiState.Loaded) -> Unit,
    modifier: Modifier = Modifier,
) {
    CountAnswersFormField(
        countAnswers = persistentListOf(
            CountAnswer(
                label = stringResource(R.string.property_registration_bedrooms_body_text),
                count = atendimentoForm.bedroomsCount,
                onCountChange = { onAtendimentoFormChange(atendimentoForm.copy(bedroomsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_en_suites_body_text),
                count = atendimentoForm.enSuitesCount,
                onCountChange = { onAtendimentoFormChange(atendimentoForm.copy(enSuitesCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_double_beds_body_text),
                count = atendimentoForm.doubleBedsCount,
                onCountChange = { onAtendimentoFormChange(atendimentoForm.copy(doubleBedsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_single_beds_body_text),
                count = atendimentoForm.singleBedsCount,
                onCountChange = { onAtendimentoFormChange(atendimentoForm.copy(singleBedsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_bathrooms_body_text),
                count = atendimentoForm.bathroomsCount,
                onCountChange = { onAtendimentoFormChange(atendimentoForm.copy(bathroomsCount = it)) },
            ),
            CountAnswer(
                label = stringResource(R.string.property_registration_parking_spots_body_text),
                count = atendimentoForm.parkingSpotsCount,
                onCountChange = { onAtendimentoFormChange(atendimentoForm.copy(parkingSpotsCount = it)) },
            ),
        ),
        modifier = modifier
    )
}
