package com.fstengineering.leasilycrm.core.presentation.composables.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsIconDropdownMenu
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.AppPrimaryIconButton
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.AppTonalIconButton
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import kotlinx.collections.immutable.toPersistentList

@Composable
fun LeadTenantCard(
    leadTenant: Tenant,
    modifier: Modifier = Modifier,
    onCardClick: (() -> Unit)? = null,
    onEditLeadTenant: () -> Unit = {},
    onDeleteLeadTenant: () -> Unit = {},
    onNewAssistance: () -> Unit = {},
    onNewStay: () -> Unit = {},
    onViewStay: () -> Unit = {},
    hasUnfinishedAssistance: Boolean = false,
    hasUnfinishedStay: Boolean = false,
    mustShowAssistanceText: Boolean = false,
    mustShowMoreOptionsMenu: Boolean = false,
    mustShowPrimaryActions: Boolean = false,
    qtGuestsLabelText: String? = null,
) {
    AppCard(
        onClick = onCardClick,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = leadTenant.name ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (!leadTenant.city.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = leadTenant.city,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (mustShowAssistanceText) {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (hasUnfinishedAssistance) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Group,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = qtGuestsLabelText
                                    ?: stringResource(R.string.lead_searching_qt_guests_empty_text),
                                style = MaterialTheme.typography.bodyMedium
                                    .copy(fontStyle = if (qtGuestsLabelText != null) null else FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.no_unfinished_assistance_text),
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }
            }

            if (mustShowMoreOptionsMenu) {
                AppMoreOptionsIconDropdownMenu(
                    options = buildList {
                        add(
                            MoreOptionsMenuOption(
                                icon = Icons.Outlined.Edit,
                                text = stringResource(R.string.edit_action_button_text),
                                onClick = onEditLeadTenant,
                            ),
                        )
                        add(
                            MoreOptionsMenuOption(
                                icon = Icons.Outlined.Delete,
                                text = stringResource(R.string.delete_action_button_text),
                                onClick = onDeleteLeadTenant,
                            ),
                        )
                    }.toPersistentList(),
                    modifier = Modifier
                        .padding(top = 16.dp, end = 16.dp)
                        .align(Alignment.TopEnd)
                )
            }

            if (mustShowPrimaryActions) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 8.dp, end = 8.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    if (!hasUnfinishedAssistance) {
                        AppTonalIconButton(
                            icon = painterResource(R.drawable.assignment_add),
                            onClick = onNewAssistance
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    AppPrimaryIconButton(
                        icon = if (hasUnfinishedStay) {
                            painterResource(R.drawable.calendar_view)
                        } else painterResource(R.drawable.calendar_add_on),
                        onClick = if (hasUnfinishedStay) onViewStay else onNewStay,
                        modifier = Modifier
                            .width(52.dp)
                    )
                }
            }
        }
    }
}
