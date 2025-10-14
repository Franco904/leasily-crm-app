package com.fstengineering.leasilycrm.home.dashboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.home.composables.HomeNavCard
import com.fstengineering.leasilycrm.home.dashboard.composables.LeadContactChannelsDistributionPieChartCard
import com.fstengineering.leasilycrm.home.dashboard.composables.MonthlyAverageTicketMetricCard
import com.fstengineering.leasilycrm.home.dashboard.composables.MonthlyCommissionInvoiceMetricCard
import com.fstengineering.leasilycrm.home.dashboard.composables.MonthlyCommissionInvoicePerPropertyOwnerSegmentMetricCard
import com.fstengineering.leasilycrm.home.dashboard.composables.MonthlyCommissionInvoicePerTenantSegmentMetricCard
import com.fstengineering.leasilycrm.home.dashboard.composables.MonthlyCommissionInvoicePerTop3NeighborhoodsMetricCard
import com.fstengineering.leasilycrm.home.dashboard.composables.MonthlyLeadGenerationMetricCard
import com.fstengineering.leasilycrm.home.dashboard.composables.MonthlyLeadGenerationPerTenantSegmentMetricCard
import com.fstengineering.leasilycrm.home.dashboard.composables.MonthlyNewStayBookingsMetricCard
import com.fstengineering.leasilycrm.home.dashboard.composables.MonthlyNewStayBookingsPerPropertyOwnerSegmentMetricCard
import com.fstengineering.leasilycrm.home.dashboard.composables.MonthlyNewStayBookingsPerTenantSegmentMetricCard
import com.fstengineering.leasilycrm.home.dashboard.composables.MonthlyNewStayBookingsPerTop3NeighborhoodsMetricCard

@Composable
fun DashboardPage(
    viewModel: DashboardViewModel,
    onPropertiesSelect: () -> Unit,
    onPropertyOwnersSelect: () -> Unit,
    onCustomerSegmentsSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Leads CRM metrics
    val generatedLeadsPerMonth by viewModel.generatedLeadsPerMonth.collectAsStateWithLifecycle()
    val generatedLeadsPerTenantSegmentPerMonth by viewModel.generatedLeadsPerTenantSegmentPerMonth.collectAsStateWithLifecycle()
    val leadContactChannelDistribution by viewModel.leadContactChannelDistribution.collectAsStateWithLifecycle()

    // Stay bookings CRM metrics
    val newStayBookingsPerMonth by viewModel.newStayBookingsPerMonth.collectAsStateWithLifecycle()
    val newStayBookingsPerTenantSegmentPerMonth by viewModel.newStayBookingsPerTenantSegmentPerMonth.collectAsStateWithLifecycle()
    val newStayBookingsPerPropertyOwnerSegmentPerMonth by viewModel.newStayBookingsPerPropertyOwnerSegmentPerMonth.collectAsStateWithLifecycle()
    val newStayBookingsPerTop3NeighborhoodsPerMonth by viewModel.newStayBookingsPerTop3NeighborhoodsPerMonth.collectAsStateWithLifecycle()

    // Commission invoice CRM metrics
    val commissionInvoicePerMonth by viewModel.commissionInvoicePerMonth.collectAsStateWithLifecycle()
    val commissionInvoicePerTenantSegmentPerMonth by viewModel.commissionInvoicePerTenantSegmentPerMonth.collectAsStateWithLifecycle()
    val commissionInvoicePerPropertyOwnerSegmentPerMonth by viewModel.commissionInvoicePerPropertyOwnerSegmentPerMonth.collectAsStateWithLifecycle()
    val commissionInvoicePerTop3NeighborhoodsPerMonth by viewModel.commissionInvoicePerTop3NeighborhoodsPerMonth.collectAsStateWithLifecycle()
    val averageTicketPerMonth by viewModel.averageTicketPerMonth.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init()
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
                    onSelect = onPropertyOwnersSelect,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            HomeNavCard(
                icon = Icons.Outlined.BookmarkBorder,
                title = stringResource(R.string.home_customer_segments_label),
                onSelect = onCustomerSegmentsSelect,
                modifier = Modifier.fillMaxWidth()
            )
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
        item { MonthlyLeadGenerationMetricCard(generatedLeadsPerMonth) }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            MonthlyLeadGenerationPerTenantSegmentMetricCard(
                generatedLeadsPerTenantSegmentPerMonth
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
        item { MonthlyNewStayBookingsMetricCard(newStayBookingsPerMonth) }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            MonthlyNewStayBookingsPerTenantSegmentMetricCard(
                newStayBookingsPerTenantSegmentPerMonth
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            MonthlyNewStayBookingsPerPropertyOwnerSegmentMetricCard(
                newStayBookingsPerPropertyOwnerSegmentPerMonth
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item {
            MonthlyNewStayBookingsPerTop3NeighborhoodsMetricCard(
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
        item { MonthlyCommissionInvoiceMetricCard(commissionInvoicePerMonth) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            MonthlyCommissionInvoicePerTenantSegmentMetricCard(
                commissionInvoicePerTenantSegmentPerMonth
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            MonthlyCommissionInvoicePerPropertyOwnerSegmentMetricCard(
                commissionInvoicePerPropertyOwnerSegmentPerMonth
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            MonthlyCommissionInvoicePerTop3NeighborhoodsMetricCard(
                commissionInvoicePerTop3NeighborhoodsPerMonth
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { MonthlyAverageTicketMetricCard(averageTicketPerMonth) }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }

}
