package com.fstengineering.leasilycrm.stayRegistration.presentation.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.presentation.composables.cards.LeadTenantCard
import kotlinx.collections.immutable.ImmutableList

@Composable
fun StayTenantSelectionList(
    tenants: ImmutableList<Tenant>,
    onTenantSelect: (Tenant) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        itemsIndexed(
            items = tenants,
            key = { _, tenant -> tenant.id!! },
            contentType = { _, entryType -> entryType },
        ) { _, tenant ->
            LeadTenantCard(
                leadTenant = tenant,
                onCardClick = { onTenantSelect(tenant) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}
