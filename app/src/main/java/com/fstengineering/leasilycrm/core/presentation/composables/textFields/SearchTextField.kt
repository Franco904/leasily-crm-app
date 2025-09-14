package com.fstengineering.leasilycrm.core.presentation.composables.textFields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun SearchTextField(
    searchInput: String,
    onSearchInputChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fieldFocusRequester: FocusRequester? = null,
) {
    TextField(
        value = searchInput,
        onValueChange = { onSearchInputChange(it) },
        placeholder = {
            Text(
                text = stringResource(R.string.search_property_placeholder),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        shape = CircleShape,
        modifier = modifier
            .fillMaxWidth()
            .then(
                fieldFocusRequester?.let { Modifier.focusRequester(it) } ?: Modifier
            )
    )
}