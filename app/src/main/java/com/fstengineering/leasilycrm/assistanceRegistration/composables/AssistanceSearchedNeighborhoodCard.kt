package com.fstengineering.leasilycrm.assistanceRegistration.composables

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
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsMenu
import com.fstengineering.leasilycrm.core.presentation.composables.MoreOptionsMenuOption
import kotlinx.collections.immutable.persistentListOf

@Composable
fun AssistanceSearchedNeighborhoodCard(
    searchedNeighborhood: AssistanceSearchedNeighborhood,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
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
                text = searchedNeighborhood.neighborhood!!,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
            )
            AppMoreOptionsMenu(
                options = persistentListOf(
                    MoreOptionsMenuOption(
                        icon = Icons.Outlined.Edit,
                        text = stringResource(R.string.edit_action_button_text),
                        onClick = onEdit,
                    ),
                    MoreOptionsMenuOption(
                        icon = Icons.Outlined.Delete,
                        text = stringResource(R.string.delete_action_button_text),
                        onClick = onDelete,
                    ),
                ),
            )
        }
    }
}
