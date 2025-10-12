package com.fstengineering.leasilycrm.home.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.fstengineering.leasilycrm.home.composables.LeadContactChannelsDistributionPieChartCard
import com.fstengineering.leasilycrm.home.composables.MonthlyLeadGenerationMetricCard
import com.fstengineering.leasilycrm.home.composables.MonthlyLeadGenerationPerTenantSegmentMetricCard

@Composable
fun DashboardPage(
    viewModel: DashboardViewModel,
    onPropertiesSelect: () -> Unit,
    onPropertyOwnersSelect: () -> Unit,
    onCustomerSegmentsSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val generatedLeadsPerMonth by viewModel.generatedLeadsPerMonth.collectAsStateWithLifecycle()
    val generatedLeadsPerTenantSegmentPerMonth by viewModel.generatedLeadsPerTenantSegmentPerMonth.collectAsStateWithLifecycle()
    val leadContactChannelDistribution by viewModel.leadContactChannelDistribution.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Row {
            HomeNavCard(
                icon = Icons.Default.Apartment,
                title = stringResource(R.string.home_properties_label),
                onSelect = onPropertiesSelect,
                modifier = Modifier
                    .weight(1f)
            )
            HomeNavCard(
                icon = Icons.Outlined.Person,
                title = stringResource(R.string.home_property_owners_label),
                onSelect = onPropertyOwnersSelect,
                modifier = Modifier
                    .weight(1f)
            )
        }
        HomeNavCard(
            icon = Icons.Outlined.BookmarkBorder,
            title = stringResource(R.string.home_customer_segments_label),
            onSelect = onCustomerSegmentsSelect,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.dashboard_my_metrics_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.dashboard_leads_group_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        MonthlyLeadGenerationMetricCard(
            generatedLeadsPerMonth = generatedLeadsPerMonth,
        )
        Spacer(modifier = Modifier.height(8.dp))
        MonthlyLeadGenerationPerTenantSegmentMetricCard(
            generatedLeadsPerTenantSegmentPerMonth = generatedLeadsPerTenantSegmentPerMonth,
        )
        Spacer(modifier = Modifier.height(8.dp))
        LeadContactChannelsDistributionPieChartCard(
            leadContactChannelDistribution = leadContactChannelDistribution,
        )
        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.dashboard_bookings_group_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.dashboard_invoicing_group_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
