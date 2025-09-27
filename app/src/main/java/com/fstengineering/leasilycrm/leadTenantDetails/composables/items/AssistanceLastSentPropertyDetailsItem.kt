package com.fstengineering.leasilycrm.leadTenantDetails.composables.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.presentation.composables.PropertiesEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.cards.PropertyCard

@Composable
fun AssistanceLastSentPropertyDetailsItem(
    lastSentProperty: Property?,
    onViewAll: () -> Unit,
    onViewSentPropertyDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.lead_tenant_assistance_details_last_sent_property_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
            )
            if (lastSentProperty != null) {
                TextButton(onClick = onViewAll) {
                    Text(
                        text = stringResource(R.string.view_all_o_action_button_text),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(20.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (lastSentProperty == null) {
            PropertiesEmptySection(
                isSearchingProperty = false,
                modifier = Modifier
                    .padding(8.dp)
            )
        } else {
            PropertyCard(
                property = lastSentProperty,
                onCardClick = onViewSentPropertyDetails,
                hasMoreOptionsMenu = false,
                hasCharacteristicsChipsRow = false,
            )
        }
    }
}
