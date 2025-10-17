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
import com.fstengineering.leasilycrm.core.domain.models.PropertyPrice
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsIconDropdownMenu
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PropertyPriceCard(
    monthLabel: String,
    monthPrices: ImmutableList<PropertyPrice>,
    modifier: Modifier = Modifier,
    onEditPropertyPrice: (PropertyPrice) -> Unit = {},
    onDeletePropertyPrice: (PropertyPrice) -> Unit = {},
    hasMoreOptionsMenu: Boolean = true,
) {
    AppCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = monthLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            monthPrices.forEach { price ->
                val priceAmountFormatted = price.price
                    .toString()
                    .replace(".", ",")

                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.property_registration_date_range_value,
                            price.startDate?.dayOfMonth.toString(),
                            price.startDate?.monthValue.toString(),
                            price.endDate?.dayOfMonth.toString(),
                            price.endDate?.monthValue.toString(),
                        ),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                    )
                    Text(
                        text = "R$ $priceAmountFormatted",
                        modifier = Modifier
                            .padding(end = if (hasMoreOptionsMenu) 0.dp else 4.dp)
                    )
                    if (hasMoreOptionsMenu) {
                        Spacer(modifier = Modifier.width(24.dp))
                        AppMoreOptionsIconDropdownMenu(
                            options = persistentListOf(
                                MoreOptionsMenuOption(
                                    icon = Icons.Outlined.Edit,
                                    text = stringResource(R.string.edit_action_button_text),
                                    onClick = { onEditPropertyPrice(price) },
                                ),
                                MoreOptionsMenuOption(
                                    icon = Icons.Outlined.Delete,
                                    text = stringResource(R.string.delete_action_button_text),
                                    onClick = { onDeletePropertyPrice(price) },
                                ),
                            ),
                            modifier = Modifier
                                .padding(end = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            HorizontalDivider()
        }
    }
}
