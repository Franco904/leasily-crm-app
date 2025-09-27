package com.fstengineering.leasilycrm.leadTenantDetails.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.PlaylistRemove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsMenu
import com.fstengineering.leasilycrm.core.presentation.composables.MoreOptionsMenuOption
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadTenantDetailsTopAppBar(
    titleText: String,
    hasUnfinishedAssistance: Boolean,
    hasUnfinishedStay: Boolean,
    hasMoreOptions: Boolean,
    onBack: () -> Unit,
    onEditLeadTenant: () -> Unit,
    onDeleteLeadTenant: () -> Unit,
    onNewAssistance: () -> Unit,
    onEditAssistance: () -> Unit,
    onDeleteAssistance: () -> Unit,
    onNewStay: () -> Unit,
    onViewStay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        title = {
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(start = 8.dp)
            )
        },
        actions = {
            AnimatedVisibility(visible = hasMoreOptions) {
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
                        add(MoreOptionsMenuOption(isHorizontalDivider = true))
                        if (hasUnfinishedAssistance) {
                            addAll(
                                listOf(
                                    MoreOptionsMenuOption(
                                        icon = Icons.Outlined.EditNote,
                                        text = stringResource(R.string.edit_assistance_action_button_text),
                                        onClick = onEditAssistance,
                                    ),
                                    MoreOptionsMenuOption(
                                        icon = Icons.Outlined.PlaylistRemove,
                                        text = stringResource(R.string.delete_assistance_action_button_text),
                                        onClick = onDeleteAssistance,
                                    ),
                                    MoreOptionsMenuOption(isHorizontalDivider = true),
                                ),
                            )
                        } else {
                            addAll(
                                listOf(
                                    MoreOptionsMenuOption(
                                        iconDrawable = R.drawable.assignment_add,
                                        text = stringResource(R.string.assist_action_button_text),
                                        onClick = onNewAssistance,
                                    ),
                                    MoreOptionsMenuOption(isHorizontalDivider = true),
                                ),
                            )
                        }
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
                    }.toPersistentList(),
                    modifier = Modifier
                        .padding(end = 16.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        windowInsets = WindowInsets(0),
        modifier = modifier
    )
}
