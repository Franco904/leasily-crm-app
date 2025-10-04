package com.fstengineering.leasilycrm.core.presentation.composables.textFields

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun FormTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = { _ -> },
    onTap: (() -> Unit)? = null,
    isInvalid: Boolean = false,
    isEnabled: Boolean = true,
    isReadOnly: Boolean = false,
    isSingleLine: Boolean = true,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
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
        label = {
            Text(
                text = label,
                color = when {
                    isEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        isError = isInvalid,
        enabled = isEnabled,
        readOnly = isReadOnly,
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        ),
        textStyle = MaterialTheme.typography.bodyLarge,
        prefix = prefix,
        suffix = suffix,
        supportingText = {
            Row {
                if (supportingText != null) {
                    Text(
                        text = supportingText,
                        color = when {
                            isInvalid -> MaterialTheme.colorScheme.error
                            isEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(top = 4.dp)
                    )
                }
                if (maxLength != null) {
                    Text(
                        text = "$currentLength / $maxLength",
                        color = when {
                            isEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        },
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
        singleLine = isSingleLine,
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
            .then(
                if (!isEnabled) {
                    Modifier
                        .pointerInput(Unit) {
                            detectTapGestures { onTap?.invoke() }
                        }
                } else Modifier
            )
    )
}
