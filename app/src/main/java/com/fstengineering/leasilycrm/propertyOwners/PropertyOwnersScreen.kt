package com.fstengineering.leasilycrm.propertyOwners

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.utils.itemsIndexed
import com.fstengineering.leasilycrm.propertyOwners.PropertyOwnersViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.propertyOwners.PropertyOwnersViewModel.UiEvents.PropertyOwnerDeleteSuccess
import com.fstengineering.leasilycrm.propertyOwners.PropertyOwnersViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.propertyOwners.composables.ConfirmDeletePropertyOwnerDialog
import com.fstengineering.leasilycrm.propertyOwners.composables.PropertyOwnerCard
import com.fstengineering.leasilycrm.propertyOwners.composables.PropertyOwnersEmptySection
import com.fstengineering.leasilycrm.propertyOwners.composables.PropertyOwnersTopAppBar

@Composable
fun PropertyOwnersScreen(
    viewModel: PropertyOwnersViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onPropertyOwnerRegistration: (Int?) -> Unit = { _ -> },
    onSignOut: () -> Unit = {},
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val lazyListState = rememberLazyListState()

    val surfaceColor = MaterialTheme.colorScheme.surface

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val propertyOwners by viewModel.propertyOwners.collectAsStateWithLifecycle()
    val propertyOwnerToDelete by viewModel.propertyOwnerToDelete.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is RedirectToSignIn -> {
                    onSignOut()
                }

                is PropertyOwnerDeleteSuccess -> {
                    showSnackbar(context.getString(R.string.snackbar_delete_property_owner_succeeded))
                }
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        topBar = {
            PropertyOwnersTopAppBar(onBack = onBack)
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { onPropertyOwnerRegistration(null) },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AppCircularProgressIndicator(
                        modifier = Modifier
                            .size(64.dp)
                    )
                }
            }

            propertyOwners.isEmpty() -> {
                PropertyOwnersEmptySection()
            }

            else -> {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    itemsIndexed(
                        items = propertyOwners.groupBy {
                            it.name?.first()?.uppercaseChar().toString()
                        },
                        key = { _, (nameFirstChar, _) -> nameFirstChar },
                        contentType = { _, entryType -> entryType },
                    ) { _, (nameFirstChar, propertyOwners) ->
                        Text(
                            text = nameFirstChar,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column {
                            propertyOwners.forEach { propertyOwner ->
                                PropertyOwnerCard(
                                    propertyOwner = propertyOwner,
                                    onEditPropertyOwner = {
                                        onPropertyOwnerRegistration(propertyOwner.id)
                                    },
                                    onDeletePropertyOwner = {
                                        viewModel.onDeletePropertyOwner(propertyOwner = propertyOwner)
                                    },
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
        if (propertyOwnerToDelete != null) {
            ConfirmDeletePropertyOwnerDialog(
                onConfirmDelete = viewModel::deletePropertyOwner,
                onCancel = {
                    viewModel.onDeletePropertyOwner(propertyOwner = null)
                },
            )
        }
    }
}
