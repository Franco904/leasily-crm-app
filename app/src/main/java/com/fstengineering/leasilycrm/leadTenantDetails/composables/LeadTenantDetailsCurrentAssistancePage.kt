package com.fstengineering.leasilycrm.leadTenantDetails.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristic
import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.AppTonalButton
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.AssistanceLastInteractionDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.AssistanceLastSentPropertyDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.AssistanceLastUpdateDateDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.AssistanceObservationsDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.AssistancePreferredOtherCharacteristicsDetailsItems
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.AssistanceQuantityCharacteristicsDetailsItems
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.AssistanceSearchedNeighborhoodsDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.PreferredSeaProximityTypeDetailsItem
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadTenantDetailsCurrentAssistancePage(
    currentAssistance: Assistance?,
    assistancePropertyCharacteristics: ImmutableList<AssistanceCharacteristic>,
    lastSentProperty: Property?,
    lastInteraction: AssistanceInteraction?,
    onRegisterAssistance: () -> Unit,
    onEditAssistance: () -> Unit,
    onDeleteAssistance: () -> Unit,
    onViewAllSentProperties: () -> Unit,
    onViewAllInteractions: () -> Unit,
    onViewSentPropertyDetails: (Long) -> Unit,
    onViewInteractionDetails: (AssistanceInteraction) -> Unit,
    isViewingAllSentProperties: Boolean,
    isViewingAllInterations: Boolean,
    isBringIntoViewEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    val sentPropertiesBringIntoViewRequester = remember { BringIntoViewRequester() }
    val interactionsBringIntoViewRequester = remember { BringIntoViewRequester() }

    LaunchedEffect(isViewingAllSentProperties) {
        if (isBringIntoViewEnabled && !isViewingAllSentProperties) {
            sentPropertiesBringIntoViewRequester.bringIntoView()
        }
    }

    LaunchedEffect(isViewingAllInterations) {
        if (isBringIntoViewEnabled && !isViewingAllInterations) {
            interactionsBringIntoViewRequester.bringIntoView()
        }
    }

    AnimatedContent(
        targetState = currentAssistance != null,
    ) { hasCurrentAssistance ->
        if (!hasCurrentAssistance) {
            AssistanceEmptySection(onRegisterAssistance = onRegisterAssistance)
        } else {
            if (currentAssistance == null) return@AnimatedContent

            Column(
                modifier = modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Row {
                    AppTonalButton(
                        icon = Icons.Outlined.Edit,
                        label = stringResource(R.string.edit_action_button_text),
                        onClick = onEditAssistance,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AppTonalButton(
                        icon = Icons.Outlined.Delete,
                        label = stringResource(R.string.delete_action_button_text),
                        onClick = onDeleteAssistance,
                        modifier = Modifier
                            .weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AssistanceLastUpdateDateDetailsItem(
                    updateDateTime = currentAssistance.updateDateTime ?: AppDateTime.now(),
                )
                Spacer(modifier = Modifier.height(24.dp))
                AssistanceQuantityCharacteristicsDetailsItems(assistance = currentAssistance)
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                PreferredSeaProximityTypeDetailsItem(
                    seaProximityType = currentAssistance.seaProximityType,
                    distanceToSea = currentAssistance.distanceToSea,
                )
                Spacer(modifier = Modifier.height(24.dp))
                AssistanceSearchedNeighborhoodsDetailsItem(
                    searchedNeighborhoods = currentAssistance.searchedNeighborhoods,
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AssistancePreferredOtherCharacteristicsDetailsItems(
                    assistance = currentAssistance,
                    assistancePropertyCharacteristics = assistancePropertyCharacteristics,
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AssistanceObservationsDetailsItem(observations = currentAssistance.observations)
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AssistanceLastSentPropertyDetailsItem(
                    lastSentProperty = lastSentProperty,
                    onViewAll = onViewAllSentProperties,
                    onViewSentPropertyDetails = { onViewSentPropertyDetails(lastSentProperty?.id!!) },
                    modifier = Modifier
                        .bringIntoViewRequester(sentPropertiesBringIntoViewRequester)
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AssistanceLastInteractionDetailsItem(
                    lastInteraction = lastInteraction,
                    onViewAll = onViewAllInteractions,
                    onViewInteractionDetails = { onViewInteractionDetails(lastInteraction!!) },
                    modifier = Modifier
                        .bringIntoViewRequester(interactionsBringIntoViewRequester)
                )
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}
