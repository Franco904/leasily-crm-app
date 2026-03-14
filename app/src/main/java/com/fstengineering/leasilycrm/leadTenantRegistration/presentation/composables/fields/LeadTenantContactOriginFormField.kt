package com.fstengineering.leasilycrm.leadTenantRegistration.presentation.composables.fields

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
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadTenantContactOriginFormField(
    contactOrigin: String?,
    onContactOriginChange: (String?) -> Unit,
    pastContactOrigins: ImmutableList<String>,
    onDone: () -> Unit,
    fieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isMenuExpanded,
        onExpandedChange = {
            if (pastContactOrigins.isNotEmpty()) {
                isMenuExpanded = !isMenuExpanded
            }
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        FormTextField(
            label = stringResource(R.string.lead_tenant_registration_contact_origin_type_label),
            value = contactOrigin ?: "",
            onValueChange = { onContactOriginChange(it.capitalizeFirst()) },
            maxLength = 100,
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
            pastContactOrigins.forEach { pastContactOrigin ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = pastContactOrigin,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    onClick = {
                        onContactOriginChange(pastContactOrigin)
                        isMenuExpanded = false
                    },
                )
            }
        }
    }
}
