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
import kotlinx.collections.immutable.toPersistentList

@Composable
fun LeadTenantCard(
    leadTenant: Tenant,
    modifier: Modifier = Modifier,
    onViewLeadTenantDetails: (() -> Unit)? = null,
    onEditLeadTenant: () -> Unit = {},
    onDeleteLeadTenant: () -> Unit = {},
    onNewAssistance: () -> Unit = {},
    onNewStay: () -> Unit,
    onViewStay: () -> Unit,
    hasMoreOptionsMenu: Boolean = true,
    hasUnfinishedAssistance: Boolean = false,
    hasUnfinishedStay: Boolean = false,
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
                            if (!hasUnfinishedAssistance) {
                                val options = listOf(
                                    MoreOptionsMenuOption(isHorizontalDivider = true),
                                    MoreOptionsMenuOption(
                                        iconDrawable = R.drawable.assignment_add,
                                        text = stringResource(R.string.assist_action_button_text),
                                        onClick = onNewAssistance,
                                    ),
                                )

                                addAll(options)
                            }
                            add(MoreOptionsMenuOption(isHorizontalDivider = true))
                            if (hasUnfinishedStay) {
                                add(
                                    MoreOptionsMenuOption(
                                        iconDrawable = R.drawable.calendar_view,
                                        text = stringResource(R.string.view_property_stay_action_button_text),
                                        onClick = onViewStay,
                                    ),
                                )
                            } else {
                                add(
                                    MoreOptionsMenuOption(
                                        iconDrawable = R.drawable.calendar_add_on,
                                        text = stringResource(R.string.new_property_stay_action_button_text),
                                        onClick = onNewStay,
                                    ),
                                )
                            }
                        }.toPersistentList()
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
