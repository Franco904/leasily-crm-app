package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AppMoreOptionsIconDropdownMenu(
    options: ImmutableList<MoreOptionsMenuOption>,
    modifier: Modifier = Modifier,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
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
                Icons.Default.MoreVert,
                contentDescription = null,
            )
        }
        DropdownMenu(
            expanded = isMenuExpanded,
            shape = MaterialTheme.shapes.small,
            onDismissRequest = { isMenuExpanded = false }
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
                            isMenuExpanded = false
                        },
                    )
                }
            }
        }
    }
}
