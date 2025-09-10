package com.fstengineering.leasilycrm.propertyOwners.composables

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
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsMenu
import com.fstengineering.leasilycrm.core.presentation.composables.MoreOptionsMenuOption
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PropertyOwnerCard(
    propertyOwner: PropertyOwner,
    onEditPropertyOwner: () -> Unit,
    onDeletePropertyOwner: () -> Unit,
    onViewPropertyOwnerDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        onClick = {
            if (propertyOwner.id == null) return@AppCard
            onViewPropertyOwnerDetails(propertyOwner.id)
        },
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = propertyOwner.name!!,
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
                        onClick = onEditPropertyOwner,
                    ),
                    MoreOptionsMenuOption(
                        icon = Icons.Outlined.Delete,
                        text = stringResource(R.string.delete_action_button_text),
                        onClick = onDeletePropertyOwner,
                    ),
                ),
            )
        }
    }
}
