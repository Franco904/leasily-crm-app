package com.fstengineering.leasilycrm.stayRegistration.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.SearchTextField

@Composable
fun StayRegistrationTopAppBar(
    isEditing: Boolean,
    onBack: () -> Unit,
    onSaveStay: () -> Unit,
    modifier: Modifier = Modifier,
    isSelectingProperty: Boolean = false,
    isSelectingTenant: Boolean = false,
    mustShowSearchAction: Boolean = false,
    isSearching: Boolean = false,
    onSearch: () -> Unit = {},
    searchInput: String? = null,
    onSearchInputChange: (String) -> Unit = {},
) {
    val searchFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearching) {
        if (isSearching) searchFieldFocusRequester.requestFocus()
    }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .padding(start = 8.dp, end = 24.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = when {
                            isSelectingProperty || isSelectingTenant -> {
                                Icons.AutoMirrored.Default.ArrowBack
                            }

                            else -> Icons.Default.Close
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (isSelectingProperty || isSelectingTenant) {
                    AnimatedContent(
                        targetState = isSearching,
                        label = "StayRegistrationTopAppBarTitle",
                    ) { isSearching ->
                        if (isSearching) {
                            SearchTextField(
                                searchInput = searchInput ?: "",
                                onSearchInputChange = onSearchInputChange,
                                label = stringResource(
                                    when {
                                        isSelectingProperty -> R.string.search_property_label
                                        else -> R.string.search_tenant_label
                                    },
                                ),
                                fieldFocusRequester = searchFieldFocusRequester,
                            )
                        } else {
                            Text(
                                text = stringResource(
                                    when {
                                        isSelectingProperty -> R.string.stay_registration_top_app_bar_select_property_title
                                        else -> R.string.stay_registration_top_app_bar_select_tenant_title
                                    }
                                ),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )

                        }
                    }
                } else {
                    Text(
                        text = stringResource(
                            when {
                                isEditing -> R.string.stay_registration_top_app_bar_title_edit
                                else -> R.string.stay_registration_top_app_bar_title_create
                            },
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            when {
                mustShowSearchAction -> {
                    IconButton(
                        onClick = onSearch,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                !isSearching && !isSelectingProperty && !isSelectingTenant -> {
                    OutlinedButton(
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                        ),
                        onClick = onSaveStay,
                    ) {
                        Text(
                            text = stringResource(R.string.save_action_button_text),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
