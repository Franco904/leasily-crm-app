package com.fstengineering.leasilycrm.properties

import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.PropertyCard
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeletePropertyDialog
import com.fstengineering.leasilycrm.properties.PropertiesViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.properties.PropertiesViewModel.UiEvents.PropertyDeleteSuccess
import com.fstengineering.leasilycrm.properties.PropertiesViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.properties.composables.PropertiesEmptySection
import com.fstengineering.leasilycrm.properties.composables.PropertiesTopAppBar

@Composable
fun PropertiesScreen(
    viewModel: PropertiesViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onRegisterProperty: (Int?) -> Unit = { _ -> },
    onViewPropertyDashboard: (Int) -> Unit = { _ -> },
    onViewPropertyDetails: (Int) -> Unit = { _ -> },
    onSignOut: () -> Unit = {},
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val lazyListState = rememberLazyListState()

    val surfaceColor = MaterialTheme.colorScheme.surface

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val properties by viewModel.properties.collectAsStateWithLifecycle()
    val propertyToDelete by viewModel.propertyToDelete.collectAsStateWithLifecycle()

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

                is PropertyDeleteSuccess -> {
                    showSnackbar(context.getString(R.string.snackbar_delete_property_succeeded))
                }
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        topBar = {
            PropertiesTopAppBar(
                onBack = onBack,
                onSearchProperty = {},
            )
        },
        floatingActionButton = {
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

            properties.isEmpty() -> {
                PropertiesEmptySection()
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
                        items = properties,
                        key = { _, property -> property.id!! },
                        contentType = { _, entryType -> entryType },
                    ) { _, property ->
                        PropertyCard(
                            property = property,
                            onEditProperty = { onRegisterProperty(property.id) },
                            onDeleteProperty = { viewModel.onDeleteProperty(property = property) },
                            onViewPropertyDashboard = { onViewPropertyDashboard(property.id!!) },
                            onViewPropertyDetails = { onViewPropertyDetails(property.id!!) },
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
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
    }
}
