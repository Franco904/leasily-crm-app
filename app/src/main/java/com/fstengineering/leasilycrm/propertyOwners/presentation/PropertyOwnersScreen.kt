package com.fstengineering.leasilycrm.propertyOwners.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeletePropertyOwnerDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.itemsIndexed
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.core.presentation.composables.cards.PropertyOwnerCard
import com.fstengineering.leasilycrm.propertyOwners.presentation.composables.PropertyOwnersEmptySection
import com.fstengineering.leasilycrm.propertyOwners.presentation.composables.PropertyOwnersTopAppBar

@Composable
fun PropertyOwnersScreen(
    viewModel: PropertyOwnersViewModel,
    onBack: () -> Unit,
    onRegisterPropertyOwner: (Long?) -> Unit,
    onViewPropertyOwnerDetails: (Long) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!

    val lazyListState = rememberLazyListState()

    val surfaceColor = MaterialTheme.colorScheme.surface

    val propertyOwners by viewModel.propertyOwners.collectAsStateWithLifecycle()
    val propertyOwnerToDelete by viewModel.propertyOwnerToDelete.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is PropertyOwnersViewModel.UiEvent.BottomSheetError -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is PropertyOwnersViewModel.UiEvent.PropertyOwnerDeleteSuccess -> {
                    onUiEvent(UiEvent.ShowSnackBar(R.string.delete_property_owner_success_message))
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        topBar = {
            Column {
                PropertyOwnersTopAppBar(onBack = onBack)
                HorizontalDivider()
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { onRegisterPropertyOwner(null) },
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when {
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
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    itemsIndexed(
                        items = propertyOwners.groupBy {
                            it.name.firstOrNull()?.uppercaseChar().toString()
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
                                        onRegisterPropertyOwner(propertyOwner.id)
                                    },
                                    onDeletePropertyOwner = {
                                        viewModel.onDeletePropertyOwner(propertyOwner = propertyOwner)
                                    },
                                    onViewPropertyOwnerDetails = {
                                        onViewPropertyOwnerDetails(propertyOwner.id)
                                    },
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
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
