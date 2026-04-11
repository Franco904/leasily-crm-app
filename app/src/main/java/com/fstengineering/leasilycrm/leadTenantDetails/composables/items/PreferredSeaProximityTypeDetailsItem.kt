package com.fstengineering.leasilycrm.leadTenantDetails.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.constants.DISTANCE_TO_SEA_PROXIMITY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem

@Composable
fun PreferredSeaProximityTypeDetailsItem(
    seaProximityType: PropertySeaProximityType?,
    distanceToSea: AppDecimal?,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        icon = Icons.Outlined.BeachAccess,
        label = stringResource(R.string.assistance_registration_sea_proximity_type_title),
        value = when {
            seaProximityType == null -> {
                stringResource(R.string.not_registered_info_body_text)
            }

            seaProximityType.id == DISTANCE_TO_SEA_PROXIMITY_TYPE_ID -> {
                stringResource(
                    R.string.properties_sea_proximity_distance_to_sea,
                    distanceToSea ?: "",
                )
            }

            else -> seaProximityType.name ?: ""
        },
        isEmptyText = seaProximityType == null,
        modifier = modifier
    )
}
