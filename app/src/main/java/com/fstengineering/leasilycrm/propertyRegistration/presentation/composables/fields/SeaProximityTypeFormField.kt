package com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.helpers.toDecimalOrNull
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.presentation.composables.AppRadioGroup
import com.fstengineering.leasilycrm.core.presentation.composables.AppRadioOption
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyDraftFormUiState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SeaProximityTypeFormField(
    form: PropertyDraftFormUiState.Loaded,
    onFormChange: (PropertyDraftFormUiState.Loaded) -> Unit,
    onDistanceToSeaPosition: (LayoutCoordinates) -> Unit,
    distanceToSeaError: Int?,
    modifier: Modifier = Modifier,
) {
    val selectedSeaProximityTypeId = form.seaProximityTypeId
    val distanceToSea = form.distanceToSea

    AppRadioGroup(
        radioOptions = persistentListOf(
            AppRadioOption(
                type = PropertySeaProximityType.SEA_ACCESS_ID,
                label = stringResource(R.string.sea_access_property_sea_proximity_type_text),
            ),
            AppRadioOption(
                type = PropertySeaProximityType.SEA_VIEW_ID,
                label = stringResource(R.string.sea_view_property_sea_proximity_type_text),
            ),
            AppRadioOption(
                type = PropertySeaProximityType.FAR_FROM_SEA_ID,
                label = stringResource(R.string.far_from_sea_property_sea_proximity_type_text),
            ),
            AppRadioOption(
                type = PropertySeaProximityType.DISTANCE_TO_SEA_ID,
                label = stringResource(R.string.distance_to_sea_property_sea_proximity_type_text),
                contentOnCheck = {
                    AnimatedVisibility(
                        visible = selectedSeaProximityTypeId == PropertySeaProximityType.DISTANCE_TO_SEA_ID,
                        enter = slideInVertically() + fadeIn(),
                        exit = ExitTransition.None,
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            DistanceToSeaFormField(
                                value = distanceToSea?.toString(),
                                onValueChange = {
                                    onFormChange(
                                        form.copy(distanceToSea = it.toDecimalOrNull())
                                    )
                                },
                                error = distanceToSeaError,
                                modifier = Modifier
                                    .onGloballyPositioned(onDistanceToSeaPosition)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            ),
        ),
        selectedOptionLabel = selectedSeaProximityTypeId,
        onOptionSelected = { typeId ->
            val newForm = form.copy(seaProximityTypeId = typeId)
            onFormChange(newForm)
        },
        modifier = modifier
    )
}
