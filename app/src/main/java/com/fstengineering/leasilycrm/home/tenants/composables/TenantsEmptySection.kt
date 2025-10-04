package com.fstengineering.leasilycrm.home.tenants.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.home.tenants.models.TenantFilterType

@Composable
fun TenantsEmptySection(
    isSearchingTenant: Boolean,
    modifier: Modifier = Modifier,
    tenantFilterType: TenantFilterType? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Outlined.Group,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(36.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(
                when {
                    isSearchingTenant -> R.string.tenants_empty_search_text
                    tenantFilterType == TenantFilterType.UpcomingStay -> R.string.tenants_upcoming_stay_empty_text
                    tenantFilterType == TenantFilterType.Old -> R.string.tenants_old_empty_text
                    else -> R.string.tenants_generic_empty_text
                },
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
    }
}
