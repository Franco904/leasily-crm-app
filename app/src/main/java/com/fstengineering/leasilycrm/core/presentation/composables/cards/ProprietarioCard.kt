package com.fstengineering.leasilycrm.core.presentation.composables.cards

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
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsIconDropdownMenu
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import com.fstengineering.leasilycrm.core.presentation.models.ProprietarioUiState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ProprietarioCard(
    proprietario: ProprietarioUiState,
    onViewProprietarioDetails: () -> Unit,
    modifier: Modifier = Modifier,
    onEditProprietario: () -> Unit = {},
    onDeleteProprietario: () -> Unit = {},
    hasMoreOptionsMenu: Boolean = true,
) {
    AppCard(
        onClick = onViewProprietarioDetails,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = proprietario.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
            )
            if (hasMoreOptionsMenu) {
                AppMoreOptionsIconDropdownMenu(
                    options = persistentListOf(
                        MoreOptionsMenuOption(
                            icon = Icons.Outlined.Edit,
                            text = stringResource(R.string.edit_action_button_text),
                            onClick = onEditProprietario,
                        ),
                        MoreOptionsMenuOption(
                            icon = Icons.Outlined.Delete,
                            text = stringResource(R.string.delete_action_button_text),
                            onClick = onDeleteProprietario,
                        ),
                    ),
                )
            }
        }
    }
}
