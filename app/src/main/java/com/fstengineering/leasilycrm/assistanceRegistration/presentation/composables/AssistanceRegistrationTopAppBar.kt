package com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.SearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistanceRegistrationTopAppBar(
    leadTenantName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isAddingSentProperty: Boolean = false,
    mustShowSearchPropertyAction: Boolean = false,
    isSearchingPropertyToSend: Boolean = false,
    onSearchPropertyToSend: () -> Unit = {},
    searchInput: String? = null,
    onSearchInputChange: (String) -> Unit = {},
) {
    val searchFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchingPropertyToSend) {
        if (isSearchingPropertyToSend) searchFieldFocusRequester.requestFocus()
    }

    BackHandler {
        onBack()
    }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        title = {
            when {
                isAddingSentProperty -> {
                    AnimatedContent(
                        targetState = isSearchingPropertyToSend,
                        label = "AssistanceRegistrationTopAppBarTitle",
                    ) { isSearchingPropertyToSend ->
                        if (isSearchingPropertyToSend) {
                            SearchTextField(
                                searchInput = searchInput ?: "",
                                onSearchInputChange = onSearchInputChange,
                                label = stringResource(R.string.search_property_label),
                                fieldFocusRequester = searchFieldFocusRequester,
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.assistance_registration_top_app_bar_select_property_to_send_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.assistance_registration_top_app_bar_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(
                                R.string.assistance_registration_top_app_bar_subtitle,
                                leadTenantName,
                            ),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        },
        actions = {
            if (mustShowSearchPropertyAction) {
                IconButton(
                    onClick = onSearchPropertyToSend,
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
            .padding(vertical = 8.dp)
    )
}
