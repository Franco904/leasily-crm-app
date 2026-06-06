package com.fstengineering.leasilycrm.core.presentation.composables.textFields

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
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
    isPlaceholder: Boolean = false,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    maxLength: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
    onDone: KeyboardActionScope.() -> Unit = {},
    fieldFocusRequester: FocusRequester? = null,
    onFocusLost: (() -> Unit)? = null,
) {
    val density = LocalDensity.current

    var currentLength by remember(value) { mutableIntStateOf(value.length) }

    var wasFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val colors = OutlinedTextFieldDefaults.colors(
        disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    )

    val textColor = when {
        !isEnabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    val cursorColor = if (isInvalid) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.primary

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect {
            if (it is PressInteraction.Release) onTap?.invoke()
        }
    }

    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                if (maxLength == null) {
                    onValueChange(newValue)
                    return@BasicTextField
                }
                if (newValue.length <= maxLength) {
                    onValueChange(newValue)
                }
            },
            enabled = isEnabled,
            readOnly = isReadOnly,
            textStyle = MaterialTheme.typography.bodyLarge.merge(TextStyle(color = textColor)),
            cursorBrush = SolidColor(cursorColor),
            singleLine = isSingleLine,
            keyboardActions = KeyboardActions(
                onDone = if (isSingleLine) onDone else null,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = if (isSingleLine) ImeAction.Done else ImeAction.Default,
                keyboardType = keyboardType,
            ),
            interactionSource = interactionSource,
            modifier = modifier
                .fillMaxWidth()
                .onFocusChanged { state ->
                    if (wasFocused && !state.isFocused) onFocusLost?.invoke()
                    wasFocused = state.isFocused
                }
                .then(
                    fieldFocusRequester?.let { Modifier.focusRequester(it) } ?: Modifier
                )
                .then(
                    if (!isEnabled) {
                        Modifier.pointerInput(Unit) {
                            detectTapGestures { onTap?.invoke() }
                        }
                    } else Modifier
                )
                .then(
                    if (!isPlaceholder) {
                        // all children accessibility nodes (label, input, supporting text) are merged into an unique semantic node
                        Modifier
                            .semantics(mergeDescendants = true) {}
                            .padding(top = with(density) { 8.sp.toDp() }) // for floating label only
                    } else Modifier
                )
                .defaultMinSize(
                    minWidth = OutlinedTextFieldDefaults.MinWidth,
                    minHeight = OutlinedTextFieldDefaults.MinHeight,
                ),
            decorationBox = { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    enabled = isEnabled,
                    singleLine = isSingleLine,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = isInvalid,
                    label = if (!isPlaceholder) {
                        {
                            Text(
                                text = label,
                                color = when {
                                    isEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    } else null,
                    placeholder = if (isPlaceholder) {
                        {
                            Text(
                                text = label,
                                color = when {
                                    isEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    } else null,
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
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                            alpha = 0.38f
                                        )
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 4.dp)
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
                    colors = colors,
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = isEnabled,
                            isError = isInvalid,
                            interactionSource = interactionSource,
                            colors = colors,
                            unfocusedBorderThickness = if (isInvalid) 2.dp else 1.dp,
                        )
                    },
                )
            },
        )
    }
}
