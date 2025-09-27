package com.fstengineering.leasilycrm.leadTenantDetails.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristic
import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.AssistanceLastInteractionDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.AssistanceLastSentPropertyDetailsItem
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
    onViewAllSentProperties: () -> Unit,
    onViewAllInteractions: () -> Unit,
    onViewSentPropertyDetails: (Long) -> Unit,
    onViewInteractionDetails: (AssistanceInteraction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    AnimatedContent(
        targetState = currentAssistance != null,
    ) { hasCurrentAssistance ->
        if (!hasCurrentAssistance) {
            AssistanceEmptySection()
        } else {
            if (currentAssistance == null) return@AnimatedContent

            Column(
                modifier = modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
            ) {
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
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                AssistanceLastInteractionDetailsItem(
                    lastInteraction = lastInteraction,
                    onViewAll = onViewAllInteractions,
                    onViewInteractionDetails = { onViewInteractionDetails(lastInteraction!!) },
                )
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}
