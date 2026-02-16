package com.fstengineering.leasilycrm.leadTenantDetails.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.LeadTenantCityDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.LeadTenantContactOriginTypeDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.LeadTenantEmailDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.LeadTenantIdentityDocumentDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.LeadTenantNameDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.LeadTenantObservationsDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.LeadTenantPhoneDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.composables.items.LeadTenantTagDetailsItem

@Composable
fun LeadTenantDetailsIdentificationPage(
    leadTenant: Tenant,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantTagDetailsItem(tagName = leadTenant.tag)
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantNameDetailsItem(name = leadTenant.name)
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantCityDetailsItem(city = leadTenant.city)
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantIdentityDocumentDetailsItem(identityDocument = leadTenant.identityDocument)
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantPhoneDetailsItem(phone = leadTenant.phone)
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantEmailDetailsItem(email = leadTenant.email)
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantContactOriginTypeDetailsItem(
            contactOriginTypeName = leadTenant.contactOrigin?.name,
        )
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantObservationsDetailsItem(
            observations = leadTenant.observations,
            isLead = leadTenant.isLead,
        )
        Spacer(modifier = Modifier.height(120.dp))
    }
}
