package com.fstengineering.leasilycrm.core.presentation.composables.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsIconDropdownMenu
import com.fstengineering.leasilycrm.core.presentation.composables.StayTransfersEmptySection
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import com.fstengineering.leasilycrm.core.presentation.models.StayTransferUiState
import com.fstengineering.leasilycrm.core.presentation.utils.formatToTwoDigitsDateTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun StayTransfersCard(
    stayTransferUiStates: ImmutableList<StayTransferUiState>,
    modifier: Modifier = Modifier,
    hasMoreOptionsMenu: Boolean = true,
    onEditTransfer: (StayTransferUiState) -> Unit = {},
    onDeleteTransfer: (StayTransferUiState) -> Unit = {},
) {
    AppCard(
        modifier = modifier
    ) {
        if (stayTransferUiStates.isEmpty()) {
            StayTransfersEmptySection(
                modifier = Modifier
                    .padding(vertical = 64.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.stay_registration_transfers_date_column_header),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(3f)
                    )
                    Text(
                        text = stringResource(R.string.stay_registration_transfers_amount_column_header),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .weight(2f)
                    )
                    Text(
                        text = stringResource(R.string.stay_registration_transfers_description_column_header),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .weight(5f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                stayTransferUiStates.forEach { transfer ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = when {
                                transfer.date != null -> {
                                    stringResource(
                                        id = R.string.stay_registration_transfers_date_column_value,
                                        transfer.date.dayOfMonth.formatToTwoDigitsDateTime(),
                                        transfer.date.month.formatToTwoDigitsDateTime(),
                                        transfer.date.year.formatToTwoDigitsDateTime(),
                                    )
                                }

                                else -> ""
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(3f)
                        )
                        Text(
                            text = when {
                                transfer.transferAmount != null -> {
                                    stringResource(
                                        id = R.string.reais_currency_value,
                                        transfer.transferAmount
                                            .toString()
                                            .replace(".", ","),
                                    )
                                }

                                else -> ""
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .weight(2f)
                        )
                        Text(
                            text = transfer.transferDescription
                                ?: stringResource(R.string.stay_registration_transfer_description_empty_text),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .weight(if (hasMoreOptionsMenu) 4f else 5f)
                        )
                        if (hasMoreOptionsMenu) {
                            Spacer(modifier = Modifier.width(16.dp))
                            AppMoreOptionsIconDropdownMenu(
                                options = persistentListOf(
                                    MoreOptionsMenuOption(
                                        icon = Icons.Outlined.Edit,
                                        text = stringResource(R.string.edit_action_button_text),
                                        onClick = { onEditTransfer(transfer) },
                                    ),
                                    MoreOptionsMenuOption(
                                        icon = Icons.Outlined.Delete,
                                        text = stringResource(R.string.delete_action_button_text),
                                        onClick = { onDeleteTransfer(transfer) },
                                    ),
                                ),
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                }
            }
        }
    }
}
