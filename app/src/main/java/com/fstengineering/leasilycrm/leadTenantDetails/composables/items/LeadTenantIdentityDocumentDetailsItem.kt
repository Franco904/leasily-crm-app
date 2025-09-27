package com.fstengineering.leasilycrm.leadTenantDetails.composables.items

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem

@Composable
fun LeadTenantIdentityDocumentDetailsItem(
    identityDocument: String?,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        iconDrawable = R.drawable.id_card,
        label = stringResource(R.string.lead_tenant_registration_identity_document_label),
        value = identityDocument ?: stringResource(R.string.not_registered_info_body_text),
        isEmptyText = identityDocument == null,
        modifier = modifier
    )
}
