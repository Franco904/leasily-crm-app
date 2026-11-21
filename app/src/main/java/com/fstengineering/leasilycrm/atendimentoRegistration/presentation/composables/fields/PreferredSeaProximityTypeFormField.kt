package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables.fields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelSeaProximityType
import com.fstengineering.leasilycrm.core.domain.valueObjects.toDecimalOrNull
import com.fstengineering.leasilycrm.core.presentation.composables.AppRadioGroup
import com.fstengineering.leasilycrm.core.presentation.composables.AppRadioOption
import com.fstengineering.leasilycrm.imovelRegistration.presentation.composables.fields.DistanceToSeaFormField
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PreferredSeaProximityTypeFormField(
    atendimentoForm: AtendimentoDraftFormUiState.Loaded,
    onAtendimentoFormChange: (AtendimentoDraftFormUiState.Loaded) -> Unit,
    distanceToSeaError: Int?,
    modifier: Modifier = Modifier,
) {
    val selectedSeaProximityTypeId = atendimentoForm.preferredSeaProximityTypeId
    val distanceToSea = atendimentoForm.preferredDistanceToSea

    AppRadioGroup(
        radioOptions = persistentListOf(
            AppRadioOption(
                type = ImovelSeaProximityType.SEA_ACCESS_ID,
                label = stringResource(R.string.sea_access_property_sea_proximity_type_text),
            ),
            AppRadioOption(
                type = ImovelSeaProximityType.SEA_VIEW_ID,
                label = stringResource(R.string.sea_view_property_sea_proximity_type_text),
            ),
            AppRadioOption(
                type = ImovelSeaProximityType.FAR_FROM_SEA_ID,
                label = stringResource(R.string.far_from_sea_property_sea_proximity_type_text),
            ),
            AppRadioOption(
                type = ImovelSeaProximityType.DISTANCE_TO_SEA_ID,
                label = stringResource(R.string.distance_to_sea_property_sea_proximity_type_text),
                contentOnCheck = {
                    AnimatedVisibility(
                        visible = selectedSeaProximityTypeId == ImovelSeaProximityType.DISTANCE_TO_SEA_ID,
                        enter = slideInVertically() + fadeIn(),
                        exit = ExitTransition.None,
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            DistanceToSeaFormField(
                                value = distanceToSea?.toString(),
                                onValueChange = {
                                    val newForm =
                                        atendimentoForm.copy(preferredDistanceToSea = it.toDecimalOrNull())
                                    onAtendimentoFormChange(newForm)
                                },
                                error = distanceToSeaError,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            ),
        ),
        selectedOptionLabel = selectedSeaProximityTypeId,
        onOptionSelected = { typeId ->
            val newForm = atendimentoForm.copy(preferredSeaProximityTypeId = typeId)
            onAtendimentoFormChange(newForm)
        },
        modifier = modifier
    )
}
