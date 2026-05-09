package com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables

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
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.LeadTenantCityDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.LeadTenantContactOriginTypeDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.LeadTenantEmailDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.LeadTenantIdentityDocumentDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.LeadTenantNameDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.LeadTenantObservationsDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.LeadTenantPhoneDetailsItem
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items.LeadTenantTagDetailsItem

@Composable
fun LeadTenantDetailsIdentificationPage(
    tag: String?,
    name: String,
    city: String?,
    identityDocument: String?,
    phone: String?,
    email: String?,
    contactOrigin: String?,
    observations: String?,
    isLead: Boolean,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantTagDetailsItem(tagName = tag)
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantNameDetailsItem(name = name)
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantCityDetailsItem(city = city)
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantIdentityDocumentDetailsItem(identityDocument = identityDocument)
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantPhoneDetailsItem(phone = phone)
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantEmailDetailsItem(email = email)
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantContactOriginTypeDetailsItem(contactOriginTypeName = contactOrigin)
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        LeadTenantObservationsDetailsItem(
            observations = observations,
            isLead = isLead,
        )
        Spacer(modifier = Modifier.height(120.dp))
    }
}
