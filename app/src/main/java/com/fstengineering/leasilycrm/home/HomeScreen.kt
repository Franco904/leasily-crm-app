package com.fstengineering.leasilycrm.home

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.theme.AppTheme
import com.fstengineering.leasilycrm.core.presentation.models.BottomNavBarDestination
import com.fstengineering.leasilycrm.home.HomeViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.home.HomeViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.home.composables.HomeBottomNavigationBar
import com.fstengineering.leasilycrm.home.composables.HomeNavCard
import com.fstengineering.leasilycrm.home.composables.HomeTopBar
import com.fstengineering.leasilycrm.home.composables.LeadContactChannelsMetricCard
import com.fstengineering.leasilycrm.home.composables.MonthlyLeadConversionMetricCard
import com.fstengineering.leasilycrm.home.composables.MonthlyRevenueMetricCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onProfileSelect: () -> Unit = {},
    onPropertiesSelect: () -> Unit = {},
    onPropertyOwnersSelect: () -> Unit = {},
    onCustomerSegmentsSelect: () -> Unit = {},
    onSignOut: () -> Unit = {},
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val scrollState = rememberScrollState()

    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceContainerColor = MaterialTheme.colorScheme.surfaceContainer

    val userProfilePictureUrl by viewModel.userProfilePictureUrl.collectAsStateWithLifecycle()

    var currentBottomNavDestination: BottomNavBarDestination by rememberSaveable {
        mutableStateOf(BottomNavBarDestination.Dashboard)
    }

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
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceContainerColor.toArgb()
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                currentBottomNavDestination = currentBottomNavDestination,
                userProfilePictureUrl = userProfilePictureUrl,
                hasSearchButton = currentBottomNavDestination in listOf(
                    BottomNavBarDestination.Leads,
                    BottomNavBarDestination.Tenants,
                ),
                onSearchSelect = {}, // TODO: Implement search leads and tenants use cases
                onProfileSelect = onProfileSelect
            )
        },
        bottomBar = {
            HomeBottomNavigationBar(
                currentDestination = currentBottomNavDestination,
                onDestinationSelect = { destination ->
                    currentBottomNavDestination = destination
                },
                upcomingStaysCount = 2, // TODO: Get real value from view model when implementing Stays use cases
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            when (currentBottomNavDestination) {
                BottomNavBarDestination.Dashboard -> {
                    DashboardBody(
                        onPropertiesSelect = onPropertiesSelect,
                        onPropertyOwnersSelect = onPropertyOwnersSelect,
                        onCustomerSegmentsSelect = onCustomerSegmentsSelect,
                    )
                }

                BottomNavBarDestination.Leads -> {}
                BottomNavBarDestination.Tenants -> {}
                BottomNavBarDestination.Stays -> {}
            }
        }
    }
}

@Composable
fun DashboardBody(
    onPropertiesSelect: () -> Unit,
    onPropertyOwnersSelect: () -> Unit,
    onCustomerSegmentsSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
    ) {
        HomeNavCard(
            icon = Icons.Default.Apartment,
            title = stringResource(R.string.home_properties_label),
            onSelect = onPropertiesSelect,
        )
        HomeNavCard(
            icon = Icons.Outlined.Person,
            title = stringResource(R.string.home_property_owners_label),
            onSelect = onPropertyOwnersSelect,
        )
        HomeNavCard(
            icon = Icons.Outlined.BookmarkBorder,
            title = stringResource(R.string.home_customer_segments_label),
            onSelect = onCustomerSegmentsSelect,
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = stringResource(R.string.dashboard_my_metrics_title),
        style = MaterialTheme.typography.titleMedium,
    )
    Spacer(modifier = Modifier.height(16.dp))
    MonthlyLeadConversionMetricCard()
    Spacer(modifier = Modifier.height(8.dp))
    MonthlyRevenueMetricCard()
    Spacer(modifier = Modifier.height(8.dp))
    LeadContactChannelsMetricCard()
    Spacer(modifier = Modifier.height(16.dp))
}

@Preview
@Composable
fun HomeScreenPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        HomeScreen(
            viewModel = koinViewModel(),
            modifier = modifier
        )
    }
}
