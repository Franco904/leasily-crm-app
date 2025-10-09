package com.fstengineering.leasilycrm.propertyStays

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.StayTypeDialog
import com.fstengineering.leasilycrm.propertyStays.PropertyStaysViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.propertyStays.PropertyStaysViewModel.UiEvents.NavigateUp
import com.fstengineering.leasilycrm.propertyStays.PropertyStaysViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.propertyStays.composables.PropertyStaysSection
import com.fstengineering.leasilycrm.propertyStays.composables.PropertyStaysTopAppBar

@Composable
fun PropertyStaysScreen(
    propertyId: Long,
    viewModel: PropertyStaysViewModel,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onNewStay: (Boolean) -> Unit,
    onViewStay: (Long) -> Unit,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val property by viewModel.property.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val selectedYearStaysDates by viewModel.selectedYearStaysDates.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var mustShowStayTypeDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(propertyId = propertyId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is RedirectToSignIn -> {
                    onSignOut()
                }

                is NavigateUp -> {
                    onBack()
                }
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        topBar = { PropertyStaysTopAppBar(onBack = onBack) },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { mustShowStayTypeDialog = true },
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar_add_on),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) { innerPadding ->
        PropertyStaysSection(
            property = property,
            selectedYearStaysDates = selectedYearStaysDates,
            selectedYear = selectedYear,
            onYearSelect = viewModel::updatePropertyStaysSelectedYear,
            onViewStay = onViewStay,
            isLoading = isLoading,
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        )
        if (mustShowStayTypeDialog) {
            StayTypeDialog(
                onTenantStayType = {
                    mustShowStayTypeDialog = false
                    onNewStay(false)
                },
                onUnavailablePeriodStayType = {
                    mustShowStayTypeDialog = false
                    onNewStay(true)
                },
                onCancel = { mustShowStayTypeDialog = false }
            )
        }
    }
}
