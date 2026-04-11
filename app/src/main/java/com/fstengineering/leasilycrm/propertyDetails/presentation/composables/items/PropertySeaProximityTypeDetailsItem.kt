package com.fstengineering.leasilycrm.propertyDetails.presentation.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem
import com.fstengineering.leasilycrm.core.presentation.models.PropertySeaProximityTypeDetails

@Composable
fun PropertySeaProximityTypeDetailsItem(
    seaProximityTypeId: Long?,
    distanceToSea: AppDecimal?,
    modifier: Modifier = Modifier,
) {
    IconLabelValueDetailsItem(
        icon = Icons.Outlined.BeachAccess,
        label = stringResource(R.string.property_registration_sea_proximity_type_title),
        value = when (seaProximityTypeId) {
            null -> stringResource(R.string.not_registered_info_body_text)

            PropertySeaProximityType.DISTANCE_TO_SEA_ID -> {
                stringResource(
                    R.string.properties_sea_proximity_distance_to_sea,
                    distanceToSea ?: R.string.not_registered_info_body_text,
                )
            }

            else -> PropertySeaProximityTypeDetails.fromId(seaProximityTypeId)?.name
                ?: stringResource(R.string.not_registered_info_body_text)
        },
        isEmptyText = seaProximityTypeId == null,
        modifier = modifier
    )
}
