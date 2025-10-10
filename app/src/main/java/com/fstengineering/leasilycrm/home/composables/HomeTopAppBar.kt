package com.fstengineering.leasilycrm.home.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.SearchTextField
import com.fstengineering.leasilycrm.home.models.BottomNavBarDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    currentBottomNavDestination: BottomNavBarDestination,
    hasSearchButton: Boolean,
    userProfilePictureUrl: String?,
    isSearching: Boolean,
    searchInput: String?,
    onSearchInputChange: (String) -> Unit,
    onSearchSelect: () -> Unit,
    onBack: () -> Unit,
    onSettingsSelect: () -> Unit,
    onProfileSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val searchFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearching) {
        if (isSearching) searchFieldFocusRequester.requestFocus()
    }

    TopAppBar(
        navigationIcon = {
            if (isSearching) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        title = {
            AnimatedContent(
                targetState = isSearching,
                label = "HomeTopAppBarTitle",
            ) { isSearching ->
                if (isSearching) {
                    SearchTextField(
                        searchInput = searchInput ?: "",
                        onSearchInputChange = onSearchInputChange,
                        label = when {
                            currentBottomNavDestination == BottomNavBarDestination.Leads -> {
                                stringResource(R.string.search_lead_label)
                            }

                            else -> stringResource(R.string.search_tenant_label)
                        },
                        fieldFocusRequester = searchFieldFocusRequester,
                    )
                } else {
                    Text(
                        text = stringResource(currentBottomNavDestination.textId),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )
                }
            }
        },
        actions = {
            if (!isSearching) {
                if (hasSearchButton) {
                    Row {
                        IconButton(
                            onClick = onSearchSelect,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                IconButton(
                    onClick = onSettingsSelect,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                AsyncImage(
                    model = userProfilePictureUrl,
                    contentDescription = null,
                    modifier = modifier
                        .padding(end = 16.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onProfileSelect() }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        windowInsets = WindowInsets(0),
        modifier = modifier
    )
}
