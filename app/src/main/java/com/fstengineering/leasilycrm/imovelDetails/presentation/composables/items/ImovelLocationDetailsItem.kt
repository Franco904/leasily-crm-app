package com.fstengineering.leasilycrm.imovelDetails.presentation.composables.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconValueDetailsItem
import com.fstengineering.leasilycrm.core.presentation.utils.orNotRegistered
import com.fstengineering.leasilycrm.imovelDetails.presentation.models.ImovelAddressUiState

@Composable
fun ImovelLocationDetailsItem(
    imovelAddress: ImovelAddressUiState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val notRegisteredString =
        remember { context.getString(R.string.not_registered_info_var_1_body_text).lowercase() }

    val numberText = if (imovelAddress.number != null) ", ${imovelAddress.number}" else ""

    val hasApartmentSpecificCharacteristics = imovelAddress.parkingSpotNumber != null
            || imovelAddress.parkingBasementLevelNumber != null
            || imovelAddress.hobbyBoxNumber != null

    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.property_details_location_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = imovelAddress.neighborhood ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${imovelAddress.streetAddress}$numberText",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (imovelAddress.cepPostalCode != null) {
                    Text(
                        text = imovelAddress.cepPostalCode,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        if (hasApartmentSpecificCharacteristics) {
            val parkingSpotNumber =
                imovelAddress.parkingSpotNumber.orNotRegistered(notRegisteredString)
            val parkingBasementLevelNumber =
                imovelAddress.parkingBasementLevelNumber.orNotRegistered(notRegisteredString)
            val hobbyBoxNumber = imovelAddress.hobbyBoxNumber.orNotRegistered(notRegisteredString)

            Spacer(modifier = Modifier.height(16.dp))
            IconValueDetailsItem(
                icon = Icons.Outlined.DirectionsCar,
                value = stringResource(
                    id = R.string.property_details_apartment_specific_characteristics_body_text,
                    parkingSpotNumber, parkingBasementLevelNumber, hobbyBoxNumber,
                ),
            )
        }
    }
}
