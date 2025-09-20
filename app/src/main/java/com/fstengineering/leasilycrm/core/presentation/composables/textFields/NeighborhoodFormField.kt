package com.fstengineering.leasilycrm.core.presentation.composables.textFields

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeighborhoodFormField(
    value: String?,
    onValueChange: (String) -> Unit,
    pastNeighborhoods: ImmutableList<String>,
    modifier: Modifier = Modifier,
    error: Int? = null,
    hasSupportingText: Boolean = true,
    onDone: (() -> Unit)? = null,
    fieldFocusRequester: FocusRequester? = null,
    hasClearButton: Boolean = false,
    onClearValue: (() -> Unit)? = null,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExposedDropdownMenuBox(
            expanded = isMenuExpanded,
            onExpandedChange = {
                if (pastNeighborhoods.isNotEmpty()) {
                    isMenuExpanded = !isMenuExpanded
                }
            },
            modifier = Modifier
                .weight(1f)
        ) {
            FormTextField(
                label = stringResource(R.string.property_registration_neighborhood_label),
                value = value ?: "",
                onValueChange = { onValueChange(it.capitalizeFirst()) },
                isInvalid = error != null,
                supportingText = if (hasSupportingText) stringResource(
                    error ?: R.string.inline_required
                ) else null,
                maxLength = 75,
                onDone = { onDone?.invoke() },
                fieldFocusRequester = fieldFocusRequester,
                modifier = Modifier
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryEditable,
                        enabled = true,
                    )
            )
            ExposedDropdownMenu(
                expanded = isMenuExpanded,
                shape = MaterialTheme.shapes.small,
                onDismissRequest = { isMenuExpanded = false }
            ) {
                pastNeighborhoods.map { pastNeighborhood ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = pastNeighborhood,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        },
                        onClick = {
                            onValueChange(pastNeighborhood)
                            isMenuExpanded = false
                        },
                    )
                }
            }
        }
        if (hasClearButton) {
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { onClearValue?.invoke() },
                modifier = Modifier
                    .offset(y = (-8).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
