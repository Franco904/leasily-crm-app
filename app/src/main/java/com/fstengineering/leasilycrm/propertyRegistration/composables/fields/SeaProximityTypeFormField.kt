package com.fstengineering.leasilycrm.propertyRegistration.composables.fields

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
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.domain.helpers.toDecimalOrNull
import com.fstengineering.leasilycrm.core.domain.constants.DISTANCE_TO_SEA_PROXIMITY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.presentation.composables.AppRadioGroup
import com.fstengineering.leasilycrm.core.presentation.composables.AppRadioOption
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun SeaProximityTypeFormField(
    propertyForm: Property,
    onPropertyFormChange: (Property) -> Unit,
    onDistanceToSeaPosition: (LayoutCoordinates) -> Unit,
    distanceToSeaError: Int?,
    seaProximityTypes: ImmutableList<PropertySeaProximityType>,
    modifier: Modifier = Modifier,
) {
    val selectedSeaProximityType = propertyForm.seaProximityType
    val distanceToSea = propertyForm.distanceToSea

    AppRadioGroup(
        radioOptions = seaProximityTypes.map { type ->
            AppRadioOption(
                label = type.name ?: "",
                relatedContent = {
                    val isDistanceToSeaType = type.id == DISTANCE_TO_SEA_PROXIMITY_TYPE_ID
                            && selectedSeaProximityType?.id == DISTANCE_TO_SEA_PROXIMITY_TYPE_ID

                    AnimatedVisibility(
                        isDistanceToSeaType,
                        enter = slideInVertically() + fadeIn(),
                        exit = ExitTransition.None,
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            DistanceToSeaFormField(
                                value = distanceToSea?.toString(),
                                onValueChange = {
                                    onPropertyFormChange(
                                        propertyForm.copy(distanceToSea = it.toDecimalOrNull())
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
            )
        }.toImmutableList(),
        selectedOptionLabel = selectedSeaProximityType?.name,
        onOptionSelected = { typeName ->
            val type = seaProximityTypes.find { it.name == typeName }

            val newForm = propertyForm.copy(
                seaProximityType = type,
                distanceToSea = when {
                    type?.id != DISTANCE_TO_SEA_PROXIMITY_TYPE_ID -> null
                    else -> propertyForm.distanceToSea
                },
            )
            onPropertyFormChange(newForm)
        },
        modifier = modifier
    )
}
