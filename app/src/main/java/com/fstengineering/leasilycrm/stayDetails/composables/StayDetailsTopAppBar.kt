package com.fstengineering.leasilycrm.stayDetails.composables

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.EditCalendar
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsIconDropdownMenu
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayDetailsTopAppBar(
    isUnavailablePeriod: Boolean,
    isCheckOutDone: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
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
                text = stringResource(
                    when {
                        isUnavailablePeriod -> R.string.stay_unavailable_period_details_top_app_bar_title
                        else -> R.string.stay_details_top_app_bar_title
                    },
                ),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 8.dp)
            )
        },
        actions = {
            AppMoreOptionsIconDropdownMenu(
                options = buildList {
                    add(
                        MoreOptionsMenuOption(
                            icon = Icons.Outlined.EditCalendar,
                            text = stringResource(R.string.edit_action_button_text),
                            onClick = onEdit,
                        ),
                    )
                    if (!isCheckOutDone) {
                        add(
                            MoreOptionsMenuOption(
                                iconDrawable = R.drawable.event_busy,
                                text = stringResource(R.string.delete_action_button_text),
                                onClick = onDelete,
                            ),
                        )
                    }
                }.toPersistentList(),
                modifier = Modifier
                    .padding(end = 16.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        windowInsets = WindowInsets(0),
        modifier = modifier
    )
}
