package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@Composable
fun <T> AppRadioGroup(
    radioOptions: ImmutableList<AppRadioOption<T>>,
    selectedOptionLabel: T?,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    groupTitle: String? = null,
    supportingText: String? = null,
    isInvalid: Boolean = false,
) {
    Column(
        modifier = modifier
    ) {
        if (groupTitle != null) {
            Text(
                text = groupTitle,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Column(
            modifier = Modifier
                .selectableGroup()
        ) {
            radioOptions
                .sorted()
                .forEachIndexed { i, option ->
                    if (i > 0) Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = option == selectedOptionLabel,
                                onClick = { onOptionSelected(option.type) },
                                role = Role.RadioButton,
                            )
                    ) {
                        RadioButton(
                            selected = option.type == selectedOptionLabel,
                            onClick = null,
                        )
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(start = 8.dp)
                        )
                    }
                    option.contentOnCheck?.invoke()
                }
            if (supportingText != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isInvalid -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }
        }
    }
}

data class AppRadioOption<T>(
    val type: T,
    val label: String,
    val contentOnCheck: (@Composable () -> Unit)? = null,
) : Comparable<AppRadioOption<T>> {
    override fun compareTo(other: AppRadioOption<T>): Int {
        return label.compareTo(other.label)
    }
}
