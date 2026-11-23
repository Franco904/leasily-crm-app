package com.fstengineering.leasilycrm.core.presentation.composables.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
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
import com.fstengineering.leasilycrm.core.presentation.models.InteracaoCardUiState
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOptionUiState
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime
import kotlinx.collections.immutable.persistentListOf

@Composable
fun InteracaoCard(
    interacao: InteracaoCardUiState,
    onViewInteracaoDetails: () -> Unit,
    modifier: Modifier = Modifier,
    onEditInteracao: () -> Unit = {},
    onDeleteInteracao: () -> Unit = {},
    hasMoreOptionsMenu: Boolean = true,
) {
    AppCard(
        onClick = onViewInteracaoDetails,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(12.dp)
                    .background(MaterialTheme.colorScheme.tertiary)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Message,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(
                            R.string.assistance_registration_interaction_registration_interaction_date_value,
                            interacao.date.dayOfMonth.formatToTwoDigitsDateTime(),
                            interacao.date.month.formatToTwoDigitsDateTime(),
                            interacao.date.year,
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                    )
                    if (hasMoreOptionsMenu) {
                        AppMoreOptionsIconDropdownMenu(
                            options = persistentListOf(
                                MoreOptionsMenuOptionUiState(
                                    icon = Icons.Outlined.Edit,
                                    text = stringResource(R.string.edit_action_button_text),
                                    onClick = onEditInteracao,
                                ),
                                MoreOptionsMenuOptionUiState(
                                    icon = Icons.Outlined.Delete,
                                    text = stringResource(R.string.delete_action_button_text),
                                    onClick = onDeleteInteracao,
                                ),
                            ),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = interacao.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}
