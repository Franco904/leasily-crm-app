package com.fstengineering.leasilycrm.leadTenantRegistration.composables.fields

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun LeadTenantSegmentFormField(
    tenantSegmentName: String?,
    onCustomerSegmentFieldTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    FormTextField(
        label = stringResource(R.string.lead_tenant_registration_customer_segment_label),
        value = tenantSegmentName ?: "",
        onTap = onCustomerSegmentFieldTap,
        suffix = {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        isReadOnly = true,
        modifier = modifier
    )
}