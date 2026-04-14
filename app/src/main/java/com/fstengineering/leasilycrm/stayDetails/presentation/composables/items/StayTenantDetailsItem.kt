package com.fstengineering.leasilycrm.stayDetails.presentation.composables.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.cards.LeadTenantCard
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantUiState

@Composable
fun StayTenantDetailsItem(
    tenant: LeadTenantUiState?,
    cameFromTenant: Boolean,
    onViewTenantDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.stay_registration_tenant_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (tenant != null) {
            LeadTenantCard(
                leadTenant = tenant,
                onCardClick = if (cameFromTenant) null else onViewTenantDetails,
            )
        }
    }
}
