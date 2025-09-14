package com.fstengineering.leasilycrm.propertyDetails.composables.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.constants.DISTANCE_TO_SEA_PROXIMITY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType
import java.math.BigDecimal

@Composable
fun PropertySeaProximityTypeDetailsItem(
    seaProximityType: PropertySeaProximityType?,
    distanceToSea: BigDecimal?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.BeachAccess,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.property_registration_sea_proximity_type_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = when {
                seaProximityType == null -> {
                    stringResource(R.string.not_registered_info_body_text)
                }

                seaProximityType.id == DISTANCE_TO_SEA_PROXIMITY_TYPE_ID -> {
                    stringResource(
                        R.string.properties_sea_proximity_distance_to_sea,
                        distanceToSea ?: "",
                    )
                }

                else -> seaProximityType.name
            },
            style = when {
                seaProximityType == null -> MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic)
                else -> MaterialTheme.typography.bodyLarge
            },
            color = when {
                seaProximityType == null -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> MaterialTheme.colorScheme.onSurface
            },
        )
    }
}
