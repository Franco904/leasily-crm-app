package com.fstengineering.leasilycrm.clienteDetails.presentation.composables

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
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.ClienteCityDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.ClienteContactOriginTypeDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.ClienteEmailDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.ClienteIdentityDocumentDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.ClienteNameDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.ClienteObservationsDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.ClientePhoneDetailsItem
import com.fstengineering.leasilycrm.clienteDetails.presentation.composables.items.ClienteTagDetailsItem

@Composable
fun ClienteDetailsIdentificationPage(
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
        ClienteTagDetailsItem(tagName = tag)
        Spacer(modifier = Modifier.height(24.dp))
        ClienteNameDetailsItem(name = name)
        Spacer(modifier = Modifier.height(24.dp))
        ClienteCityDetailsItem(city = city)
        Spacer(modifier = Modifier.height(24.dp))
        ClienteIdentityDocumentDetailsItem(identityDocument = identityDocument)
        Spacer(modifier = Modifier.height(24.dp))
        ClientePhoneDetailsItem(phone = phone)
        Spacer(modifier = Modifier.height(24.dp))
        ClienteEmailDetailsItem(email = email)
        Spacer(modifier = Modifier.height(24.dp))
        ClienteContactOriginTypeDetailsItem(contactOriginTypeName = contactOrigin)
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        ClienteObservationsDetailsItem(
            observations = observations,
            isLead = isLead,
        )
        Spacer(modifier = Modifier.height(120.dp))
    }
}
