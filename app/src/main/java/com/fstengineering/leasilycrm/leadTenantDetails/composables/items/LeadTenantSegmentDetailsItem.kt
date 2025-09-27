package com.fstengineering.leasilycrm.leadTenantDetails.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem

@Composable
fun LeadTenantSegmentDetailsItem(
    segmentName: String?,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        icon = Icons.Outlined.BookmarkBorder,
        label = stringResource(R.string.lead_tenant_registration_customer_segment_label),
        value = segmentName ?: stringResource(R.string.not_registered_info_body_text),
        isEmptyText = segmentName == null,
        modifier = modifier
    )
}
