package com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables

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
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.AppTonalButton
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.AssistanceLastInteractionDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.AssistanceLastSentPropertyDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.AssistanceLastUpdateDateDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.AssistanceObservationsDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.AssistancePreferredOtherCharacteristicsDetailsItems
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.AssistanceQuantityCharacteristicsDetailsItems
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.AssistanceSearchedNeighborhoodsDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.PreferredSeaProximityTypeDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.models.AssistanceInteractionUiState
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.models.AssistanceUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadTenantDetailsCurrentAssistancePage(
    currentAssistance: AssistanceUiState?,
    onRegisterAssistance: () -> Unit,
    onEditAssistance: () -> Unit,
    onDeleteAssistance: () -> Unit,
    onViewAllSentProperties: () -> Unit,
    onViewAllInteractions: () -> Unit,
    onViewSentPropertyDetails: (Long) -> Unit,
    onViewInteractionDetails: (AssistanceInteractionUiState) -> Unit,
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
                    updateDateTime = currentAssistance.updateDateTime,
                )
                Spacer(modifier = Modifier.height(24.dp))
                AssistanceQuantityCharacteristicsDetailsItems(assistance = currentAssistance)
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                PreferredSeaProximityTypeDetailsItem(
                    seaProximityTypeId = currentAssistance.preferredSeaProximityTypeId,
                    distanceToSea = currentAssistance.distanceToSea,
                )
                Spacer(modifier = Modifier.height(24.dp))
                AssistanceSearchedNeighborhoodsDetailsItem(
                    searchedNeighborhoods = currentAssistance.preferredNeighborhoods,
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AssistancePreferredOtherCharacteristicsDetailsItems(
                    checkedCharacteristics = currentAssistance.preferredPropertyCharacteristics,
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AssistanceObservationsDetailsItem(observations = currentAssistance.observations)
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AssistanceLastSentPropertyDetailsItem(
                    lastSentProperty = currentAssistance.lastSentProperty,
                    onViewAll = onViewAllSentProperties,
                    onViewSentPropertyDetails = {
                        onViewSentPropertyDetails(currentAssistance.lastSentProperty!!.id)
                    },
                    modifier = Modifier
                        .bringIntoViewRequester(sentPropertiesBringIntoViewRequester)
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AssistanceLastInteractionDetailsItem(
                    lastInteraction = currentAssistance.lastInteraction,
                    onViewAll = onViewAllInteractions,
                    onViewInteractionDetails = {
                        onViewInteractionDetails(currentAssistance.lastInteraction!!)
                    },
                    modifier = Modifier
                        .bringIntoViewRequester(interactionsBringIntoViewRequester)
                )
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}
