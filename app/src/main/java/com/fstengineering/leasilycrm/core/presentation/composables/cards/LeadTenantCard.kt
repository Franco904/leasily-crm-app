package com.fstengineering.leasilycrm.core.presentation.composables.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsMenu
import com.fstengineering.leasilycrm.core.presentation.composables.MoreOptionsMenuOption
import kotlinx.collections.immutable.persistentListOf

@Composable
fun LeadTenantCard(
    leadTenant: Tenant,
    modifier: Modifier = Modifier,
    onViewLeadTenantDetails: (() -> Unit)? = null,
    onEditLeadTenant: () -> Unit = {},
    onDeleteLeadTenant: () -> Unit = {},
    onNewAssistance: () -> Unit = {},
    onViewAssistance: () -> Unit = {},
    onNewPropertyStay: () -> Unit = {},
    hasMoreOptionsMenu: Boolean = true,
    hasCharacteristicsRow: Boolean = true,
) {
    AppCard(
        onClick = onViewLeadTenantDetails,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = leadTenant.name ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                )
                if (hasMoreOptionsMenu) {
                    AppMoreOptionsMenu(
                        options = persistentListOf(
                            MoreOptionsMenuOption(
                                icon = Icons.Outlined.Edit,
                                text = stringResource(R.string.edit_action_button_text),
                                onClick = onEditLeadTenant,
                            ),
                            MoreOptionsMenuOption(
                                icon = Icons.Outlined.Delete,
                                text = stringResource(R.string.delete_action_button_text),
                                onClick = onDeleteLeadTenant,
                            ),
                            MoreOptionsMenuOption(isHorizontalDivider = true),
                            MoreOptionsMenuOption(
                                iconDrawable = R.drawable.assignment_add,
                                text = stringResource(R.string.assist_action_button_text),
                                onClick = onNewAssistance,
                            ),
                            MoreOptionsMenuOption(
                                iconDrawable = R.drawable.calendar_add_on,
                                text = stringResource(R.string.new_property_stay_action_button_text),
                                onClick = onNewPropertyStay,
                            ),
                        ),
                    )
                }
            }
            if (!leadTenant.city.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = leadTenant.city,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}
