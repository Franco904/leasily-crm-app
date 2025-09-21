package com.fstengineering.leasilycrm.core.presentation.composables.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantOrderType
import kotlinx.collections.immutable.ImmutableList

@Composable
fun OrderByTextButton(
    orderType: LeadTenantOrderType,
    orderByMenuOptions: ImmutableList<OrderByMenuOption>,
    modifier: Modifier = Modifier,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { isMenuExpanded = !isMenuExpanded },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Sort,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(
                    when (orderType) {
                        LeadTenantOrderType.Name -> R.string.order_by_name_text
                        LeadTenantOrderType.LastUpdateDate -> R.string.order_by_last_update_date_text
                    },
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        DropdownMenu(
            expanded = isMenuExpanded,
            shape = MaterialTheme.shapes.small,
            onDismissRequest = { isMenuExpanded = false },
        ) {
            orderByMenuOptions.map {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = it.text!!,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(end = 24.dp)
                        )
                    },
                    onClick = {
                        it.onClick()
                        isMenuExpanded = false
                    },
                )
            }
        }
    }
}

data class OrderByMenuOption(
    val text: String? = null,
    val onClick: () -> Unit = {},
)
