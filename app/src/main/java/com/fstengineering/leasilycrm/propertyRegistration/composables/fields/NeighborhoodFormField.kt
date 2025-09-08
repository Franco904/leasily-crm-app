package com.fstengineering.leasilycrm.propertyRegistration.composables.fields

import androidx.compose.foundation.layout.fillMaxWidth
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
import com.fstengineering.leasilycrm.core.presentation.composables.AppTextField
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeighborhoodFormField(
    value: String?,
    onValueChange: (String) -> Unit,
    pastNeighborhoods: ImmutableList<String>,
    error: Int?,
    onDone: () -> Unit,
    fieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isMenuExpanded,
        onExpandedChange = {
            if (pastNeighborhoods.isNotEmpty()) {
                isMenuExpanded = !isMenuExpanded
            }
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        AppTextField(
            label = stringResource(R.string.property_registration_neighborhood_label),
            value = value ?: "",
            onValueChange = { onValueChange(it.capitalizeFirst()) },
            isInvalid = error != null,
            supportingText = stringResource(error ?: R.string.inline_required),
            maxLength = 75,
            onDone = { onDone() },
            fieldFocusRequester = fieldFocusRequester,
            modifier = modifier
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
