package com.fstengineering.leasilycrm.stayRegistration.presentation.composables.fields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.AppOutlinedButton
import com.fstengineering.leasilycrm.core.presentation.composables.cards.PropertyCard
import com.fstengineering.leasilycrm.core.presentation.models.PropertyUiState

@Composable
fun StayPropertyFormField(
    stayProperty: PropertyUiState?,
    onSelectStayProperty: () -> Unit,
    isEnabled: Boolean,
    error: Int?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.stay_registration_property_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
            )
            AppOutlinedButton(
                icon = when {
                    stayProperty == null -> Icons.Outlined.Search
                    else -> Icons.Outlined.Edit
                },
                label = stringResource(
                    when {
                        stayProperty == null -> R.string.select_action_button_text
                        else -> R.string.edit_action_button_text
                    }
                ),
                isEnabled = isEnabled,
                onClick = onSelectStayProperty,
            )
        }
        AnimatedVisibility(stayProperty != null) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                PropertyCard(
                    property = stayProperty!!,
                    hasMoreOptionsMenu = false,
                    hasCharacteristicsChipsRow = false,
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(error ?: R.string.inline_required),
            style = MaterialTheme.typography.bodySmall,
            color = when {
                error != null -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}
