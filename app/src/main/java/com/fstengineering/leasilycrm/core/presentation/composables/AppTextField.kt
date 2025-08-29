package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AppTextField(
    placeholder: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = { _ -> },
    onTap: (() -> Unit)? = null,
    isInvalid: Boolean = false,
    isReadOnly: Boolean = false,
    suffixIcon: ImageVector? = null,
    supportingText: String? = null,
    maxLength: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
    onDone: KeyboardActionScope.() -> Unit = {},
    fieldFocusRequester: FocusRequester? = null,
) {
    var currentLength by remember(value) { mutableIntStateOf(value.length) }

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            if (it is PressInteraction.Release) onTap?.invoke()
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (maxLength == null) {
                onValueChange(newValue)
                return@OutlinedTextField
            }

            if (newValue.length <= maxLength) {
                onValueChange(newValue)
            }
        },
        isError = isInvalid,
        readOnly = isReadOnly,
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        suffix = if (suffixIcon == null) null else {
            {
                Icon(
                    imageVector = suffixIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        supportingText = {
            Row {
                if (supportingText != null) {
                    Text(
                        text = supportingText,
                        color = if (isInvalid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(top = 4.dp)
                    )
                }
                if (maxLength != null) {
                    Text(
                        text = "$currentLength / $maxLength",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = 12.dp)
                            .padding(top = 4.dp)
                    )
                }
            }
        },
        singleLine = true,
        keyboardActions = KeyboardActions(
            onDone = onDone,
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
            keyboardType = keyboardType,
        ),
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .then(
                fieldFocusRequester?.let { Modifier.focusRequester(it) } ?: Modifier
            )
    )
}
