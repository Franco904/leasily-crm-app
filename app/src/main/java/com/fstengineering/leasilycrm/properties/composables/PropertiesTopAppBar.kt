package com.fstengineering.leasilycrm.properties.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.SearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesTopAppBar(
    onBack: () -> Unit,
    isSearchingProperty: Boolean,
    searchInput: String?,
    onSearchInputChange: (String) -> Unit,
    onSearchProperty: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val searchFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchingProperty) {
        if (isSearchingProperty) searchFieldFocusRequester.requestFocus()
    }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        title = {
            AnimatedContent(
                targetState = isSearchingProperty,
                label = "PropertiesTopAppBarTitle",
            ) { isSearchingProperty ->
                if (isSearchingProperty) {
                    SearchTextField(
                        searchInput = searchInput ?: "",
                        onSearchInputChange = onSearchInputChange,
                        fieldFocusRequester = searchFieldFocusRequester,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.properties_top_app_bar_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )
                }
            }
        },
        actions = {
            if (!isSearchingProperty) {
                IconButton(
                    onClick = onSearchProperty,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        windowInsets = WindowInsets(0),
        modifier = modifier
    )
}