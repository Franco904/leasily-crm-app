package com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem

@Composable
fun LeadTenantTagDetailsItem(
    tagName: String?,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        icon = Icons.Outlined.BookmarkBorder,
        label = stringResource(R.string.lead_tenant_registration_tag_label),
        value = tagName ?: stringResource(R.string.not_registered_info_body_text),
        isEmptyText = tagName == null,
        modifier = modifier
    )
}
