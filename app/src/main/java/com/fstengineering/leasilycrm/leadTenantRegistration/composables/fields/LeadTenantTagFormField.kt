package com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import com.fstengineering.leasilycrm.core.presentation.utils.capitalizeFirst
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadTenantTagFormField(
    tag: String?,
    onTagChange: (String?) -> Unit,
    pastTags: ImmutableList<String>,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isMenuExpanded,
        onExpandedChange = {
            if (pastTags.isNotEmpty()) {
                isMenuExpanded = !isMenuExpanded
            }
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        FormTextField(
            prefix = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Label,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            label = stringResource(R.string.lead_tenant_registration_tag_label),
            value = tag ?: "",
            onValueChange = { onTagChange(it.capitalizeFirst()) },
            maxLength = 100,
            onDone = { onDone() },
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
            pastTags.map { tag ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    onClick = {
                        onTagChange(tag)
                        isMenuExpanded = false
                    },
                )
            }
        }
    }
}
