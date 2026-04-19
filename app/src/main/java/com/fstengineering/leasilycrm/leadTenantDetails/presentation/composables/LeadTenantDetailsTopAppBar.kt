package com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
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
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsIconDropdownMenu
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadTenantDetailsTopAppBar(
    titleText: String,
    hasMoreOptions: Boolean,
    onBack: () -> Unit,
    onEditLeadTenant: () -> Unit,
    onDeleteLeadTenant: () -> Unit,
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
