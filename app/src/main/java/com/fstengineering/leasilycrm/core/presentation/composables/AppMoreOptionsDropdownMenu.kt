package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AppMoreOptionsDropdownMenu(
    options: ImmutableList<MoreOptionsMenuOption>,
    modifier: Modifier = Modifier,
    isMenuExpanded: Boolean = false,
    onMenuExpanded: (Boolean) -> Unit,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
) {
    DropdownMenu(
        expanded = isMenuExpanded,
        offset = offset,
        shape = MaterialTheme.shapes.small,
        onDismissRequest = { onMenuExpanded(false) },
        modifier = modifier
    ) {
        options.map {
            if (it.isHorizontalDivider) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                DropdownMenuItem(
                    leadingIcon = when {
                        it.icon != null -> {
                            {
                                Icon(
                                    imageVector = it.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }

                        it.iconDrawable != null -> {
                            {
                                Icon(
                                    painter = painterResource(it.iconDrawable),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }

                        else -> null
                    },
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
                        onMenuExpanded(false)
                    },
                )
            }
        }
    }
}
