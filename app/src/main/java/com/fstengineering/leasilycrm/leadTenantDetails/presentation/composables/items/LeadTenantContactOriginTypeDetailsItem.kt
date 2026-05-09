package com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem

@Composable
fun LeadTenantContactOriginTypeDetailsItem(
    contactOriginTypeName: String?,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        icon = Icons.AutoMirrored.Outlined.Message,
        label = stringResource(R.string.lead_tenant_registration_contact_origin_type_label),
        value = contactOriginTypeName ?: stringResource(R.string.not_registered_info_body_text),
        isEmptyText = contactOriginTypeName == null,
        modifier = modifier
    )
}
