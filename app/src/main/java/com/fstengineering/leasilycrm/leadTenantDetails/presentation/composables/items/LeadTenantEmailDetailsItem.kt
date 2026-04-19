package com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem

@Composable
fun LeadTenantEmailDetailsItem(
    email: String?,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        icon = Icons.Outlined.Email,
        label = stringResource(R.string.lead_tenant_registration_email_label),
        value = email ?: stringResource(R.string.not_registered_info_body_text),
        isEmptyText = email == null,
        modifier = modifier
    )
}
