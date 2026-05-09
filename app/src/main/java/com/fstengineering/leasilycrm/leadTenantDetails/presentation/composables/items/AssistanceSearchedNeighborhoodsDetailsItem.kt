package com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AssistanceSearchedNeighborhoodsDetailsItem(
    searchedNeighborhoods: ImmutableList<String>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val neighborhoodsText = remember(searchedNeighborhoods) {
        searchedNeighborhoods.run {
            if (isEmpty()) return@run ""
            if (size == 1) return@run first()

            val subList = subList(0, size - 1)
            "${subList.joinToString(separator = ", ")} ${context.getString(R.string.last_separator_label)} ${last()}"
        }
    }

    IconLabelValueDetailsItem(
        icon = Icons.Outlined.LocationOn,
        label = stringResource(R.string.assistance_registration_property_location_title),
        value = when {
            searchedNeighborhoods.isEmpty() -> stringResource(R.string.not_registered_info_body_text)
            else -> neighborhoodsText
        },
        isEmptyText = searchedNeighborhoods.isEmpty(),
        modifier = modifier
    )
}
