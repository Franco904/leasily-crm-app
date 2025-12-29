package com.fstengineering.leasilycrm.properties

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.PropertiesEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.cards.PropertyCard
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeletePropertyDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.StayTypeDialog
import com.fstengineering.leasilycrm.core.presentation.models.ListState
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.properties.PropertiesViewModel.UiEvents.Error
import com.fstengineering.leasilycrm.properties.PropertiesViewModel.UiEvents.PropertyDeleteSuccess
import com.fstengineering.leasilycrm.properties.composables.PropertiesTopAppBar

@Composable
fun PropertiesScreen(
    viewModel: PropertiesViewModel,
    onBack: () -> Unit,
    onRegisterProperty: (Long?) -> Unit,
    onViewPropertyDashboard: (Long) -> Unit,
    onViewPropertyDetails: (Long) -> Unit,
    onNewStay: (Long, Boolean) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val lazyListState = rememberLazyListState()

    val surfaceColor = MaterialTheme.colorScheme.surface

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val properties by viewModel.properties.collectAsStateWithLifecycle()
    val propertyToDelete by viewModel.propertyToDelete.collectAsStateWithLifecycle()

    var isSearchingProperty by remember { mutableStateOf(false) }
    val searchInput by viewModel.searchInput.collectAsStateWithLifecycle()

    val listStatus by remember(isLoading, properties) {
        val status = when {
            isLoading -> ListState.Loading
            properties.isEmpty() -> ListState.Empty
            else -> ListState.Idle
        }

        mutableStateOf(status)
    }

    var propertyIdToRegisterStay by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is Error -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is PropertyDeleteSuccess -> {
                    onUiEvent(UiEvent.ShowSnackBar(R.string.delete_property_success_message))
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    BackHandler {
        if (isSearchingProperty) isSearchingProperty = false
        else onBack()
    }

    Scaffold(
        topBar = {
            PropertiesTopAppBar(
                onBack = {
                    if (isSearchingProperty) {
                        isSearchingProperty = false
                        viewModel.onSearchInputChange(searchInput = null)
                    } else {
                        onBack()
                    }
                },
                mustShowSearchPropertyAction = properties.isNotEmpty() && !isSearchingProperty,
                isSearchingProperty = isSearchingProperty,
                onSearchProperty = { isSearchingProperty = true },
                searchInput = searchInput,
                onSearchInputChange = viewModel::onSearchInputChange,
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                !isSearchingProperty,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { onRegisterProperty(null) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) { innerPadding ->
        AnimatedContent(
            targetState = listStatus,
        ) { listStatus ->
            when (listStatus) {
                ListState.Loading -> {
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

                ListState.Empty -> {
                    PropertiesEmptySection(isSearchingProperty = isSearchingProperty)
                }

                ListState.Idle -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        item {
                            Text(
                                text = pluralStringResource(
                                    R.plurals.properties_number_of_properties,
                                    properties.size,
                                    properties.size,
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        itemsIndexed(
                            items = properties,
                            key = { _, property -> property.id!! },
                            contentType = { _, entryType -> entryType },
                        ) { _, property ->
                            PropertyCard(
                                property = property,
                                onCardClick = { onViewPropertyDashboard(property.id!!) },
                                onViewPropertyDetails = { onViewPropertyDetails(property.id!!) },
                                onEditProperty = { onRegisterProperty(property.id) },
                                onDeleteProperty = { viewModel.onDeleteProperty(property = property) },
                                onNewStay = {
                                    propertyIdToRegisterStay = property.id!!
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        item { Spacer(modifier = Modifier.height(72.dp)) }
                    }
                }
            }
        }
        if (propertyToDelete != null) {
            ConfirmDeletePropertyDialog(
                onConfirmDelete = viewModel::deleteProperty,
                onCancel = {
                    viewModel.onDeleteProperty(property = null)
                },
            )
        }
        if (propertyIdToRegisterStay != null) {
            StayTypeDialog(
                onTenantStayType = {
                    onNewStay(propertyIdToRegisterStay!!, false)
                    propertyIdToRegisterStay = null
                },
                onUnavailablePeriodStayType = {
                    onNewStay(propertyIdToRegisterStay!!, true)
                    propertyIdToRegisterStay = null
                },
                onCancel = { propertyIdToRegisterStay = null }
            )
        }
    }
}
