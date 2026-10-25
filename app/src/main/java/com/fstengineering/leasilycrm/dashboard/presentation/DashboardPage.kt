package com.fstengineering.leasilycrm.dashboard.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.dashboard.presentation.composables.LeadContactChannelsDistributionPieChartCard
import com.fstengineering.leasilycrm.dashboard.presentation.composables.MonthlyAverageTicketAnalyticsCard
import com.fstengineering.leasilycrm.dashboard.presentation.composables.MonthlyCommissionInvoiceAnalyticsCard
import com.fstengineering.leasilycrm.dashboard.presentation.composables.MonthlyCommissionInvoicePerTenantTagAnalyticsCard
import com.fstengineering.leasilycrm.dashboard.presentation.composables.MonthlyCommissionInvoicePerTop3NeighborhoodsAnalyticsCard
import com.fstengineering.leasilycrm.dashboard.presentation.composables.MonthlyLeadGenerationAnalyticsCard
import com.fstengineering.leasilycrm.dashboard.presentation.composables.MonthlyLeadGenerationPerTenantTagAnalyticsCard
import com.fstengineering.leasilycrm.dashboard.presentation.composables.MonthlyNewStayBookingsAnalyticsCard
import com.fstengineering.leasilycrm.dashboard.presentation.composables.MonthlyNewStayBookingsPerTenantSegmentAnalyticsCard
import com.fstengineering.leasilycrm.dashboard.presentation.composables.MonthlyNewStayBookingsPerTop3NeighborhoodsAnalyticsCard
import com.fstengineering.leasilycrm.home.presentation.composables.HomeNavCard

@Composable
fun DashboardPage(
    viewModel: DashboardViewModel,
    onPropertiesSelect: () -> Unit,
    onProprietariosSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Leads CRM metrics
    val generatedLeadsPerMonth by viewModel.generatedLeadsPerMonth.collectAsStateWithLifecycle()
    val generatedLeadsPerTenantTagPerMonth by viewModel.generatedLeadsPerTenantTagPerMonth.collectAsStateWithLifecycle()
    val leadContactChannelDistribution by viewModel.leadContactChannelDistribution.collectAsStateWithLifecycle()

    // Stay bookings CRM metrics
    val newStayBookingsPerMonth by viewModel.newStayBookingsPerMonth.collectAsStateWithLifecycle()
    val newStayBookingsPerTenantTagPerMonth by viewModel.newStayBookingsPerTenantTagPerMonth.collectAsStateWithLifecycle()
    val newStayBookingsPerTop3NeighborhoodsPerMonth by viewModel.newStayBookingsPerTop3NeighborhoodsPerMonth.collectAsStateWithLifecycle()

    // Commission invoice CRM metrics
    val commissionInvoicePerMonth by viewModel.commissionInvoicePerMonth.collectAsStateWithLifecycle()
    val commissionInvoicePerTenantTagPerMonth by viewModel.commissionInvoicePerTenantTagPerMonth.collectAsStateWithLifecycle()
    val commissionInvoicePerTop3NeighborhoodsPerMonth by viewModel.commissionInvoicePerTop3NeighborhoodsPerMonth.collectAsStateWithLifecycle()
    val averageTicketPerMonth by viewModel.averageTicketPerMonth.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) viewModel.onPause()
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        // Navigation cards
        item {
            Row {
                HomeNavCard(
                    icon = Icons.Default.Apartment,
                    title = stringResource(R.string.home_properties_label),
                    onSelect = onPropertiesSelect,
                    modifier = Modifier.weight(1f)
                )
                HomeNavCard(
                    icon = Icons.Outlined.Person,
                    title = stringResource(R.string.home_property_owners_label),
                    onSelect = onProprietariosSelect,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Metrics section header
        item {
            Text(
                text = stringResource(R.string.dashboard_my_metrics_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // === Leads section ===
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            Text(
                text = stringResource(R.string.dashboard_leads_group_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { MonthlyLeadGenerationAnalyticsCard(generatedLeadsPerMonth) }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            MonthlyLeadGenerationPerTenantTagAnalyticsCard(
                generatedLeadsPerTenantTagPerMonth
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { LeadContactChannelsDistributionPieChartCard(leadContactChannelDistribution) }

        item { Spacer(modifier = Modifier.height(32.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(32.dp)) }

        // === Bookings section ===
        item {
            Text(
                text = stringResource(R.string.dashboard_stay_bookings_group_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { MonthlyNewStayBookingsAnalyticsCard(newStayBookingsPerMonth) }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            MonthlyNewStayBookingsPerTenantSegmentAnalyticsCard(
                newStayBookingsPerTenantTagPerMonth
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            MonthlyNewStayBookingsPerTop3NeighborhoodsAnalyticsCard(
                newStayBookingsPerTop3NeighborhoodsPerMonth
            )
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(32.dp)) }

        // === Commission invoice section ===
        item {
            Text(
                text = stringResource(R.string.dashboard_commission_invoice_group_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { MonthlyCommissionInvoiceAnalyticsCard(commissionInvoicePerMonth) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            MonthlyCommissionInvoicePerTenantTagAnalyticsCard(
                commissionInvoicePerTenantTagPerMonth
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            MonthlyCommissionInvoicePerTop3NeighborhoodsAnalyticsCard(
                commissionInvoicePerTop3NeighborhoodsPerMonth
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { MonthlyAverageTicketAnalyticsCard(averageTicketPerMonth) }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }

}
