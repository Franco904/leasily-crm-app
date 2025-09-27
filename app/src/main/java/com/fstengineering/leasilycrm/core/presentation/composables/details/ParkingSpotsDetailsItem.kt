package com.fstengineering.leasilycrm.core.presentation.composables.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun ParkingSpotsDetailsItem(
    qtParkingSpots: Int,
    modifier: Modifier = Modifier,
) {
    IconValueDetailsItem(
        icon = Icons.Outlined.DirectionsCar,
        value = when {
            qtParkingSpots == 0 -> {
                stringResource(R.string.property_details_no_parking_spots)
            }

            else -> {
                pluralStringResource(
                    R.plurals.property_details_qt_parking_spots,
                    qtParkingSpots,
                    qtParkingSpots,
                )
            }
        },
        modifier = modifier
    )
}
