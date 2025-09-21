package com.fstengineering.leasilycrm.core.presentation.composables.textFields

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
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
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isMenuExpanded,
        onExpandedChange = {
            if (pastNeighborhoods.isNotEmpty()) {
                isMenuExpanded = !isMenuExpanded
            }
        },
        modifier = modifier
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
}
