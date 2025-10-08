package com.fstengineering.leasilycrm.customerSegments.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsIconDropdownMenu
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import kotlinx.collections.immutable.persistentListOf

@Composable
fun CustomerSegmentCard(
    segment: CustomerSegment,
    onEditSegment: () -> Unit,
    onDeleteSegment: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = segment.name!!,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
            )
            AppMoreOptionsIconDropdownMenu(
                options = persistentListOf(
                    MoreOptionsMenuOption(
                        icon = Icons.Outlined.Edit,
                        text = stringResource(R.string.edit_action_button_text),
                        onClick = onEditSegment,
                    ),
                    MoreOptionsMenuOption(
                        icon = Icons.Outlined.Delete,
                        text = stringResource(R.string.delete_action_button_text),
                        onClick = onDeleteSegment,
                    ),
                ),
            )
        }
    }
}
