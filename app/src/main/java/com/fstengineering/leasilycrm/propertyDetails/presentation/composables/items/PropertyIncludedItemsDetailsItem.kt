package com.fstengineering.leasilycrm.propertyDetails.presentation.composables.items

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem

@Composable
fun PropertyIncludedItemsDetailsItem(
    includedItems: String?,
    hasIncludedItems: Boolean,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        iconDrawable = R.drawable.box,
        label = stringResource(R.string.property_registration_included_items_label),
        value = when {
            !hasIncludedItems || includedItems.isNullOrBlank() -> {
                stringResource(R.string.property_registration_does_not_include_items_body_text)
            }

            else -> includedItems
        },
        modifier = modifier
    )
}
