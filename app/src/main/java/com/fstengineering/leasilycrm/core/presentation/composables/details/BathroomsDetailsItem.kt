package com.fstengineering.leasilycrm.core.presentation.composables.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bathtub
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun BathroomsDetailsItem(
    qtBathrooms: Int,
    modifier: Modifier = Modifier,
) {
    IconValueDetailsItem(
        icon = Icons.Outlined.Bathtub,
        value = when {
            qtBathrooms == 0 -> {
                stringResource(R.string.property_details_no_bathrooms)
            }

            else -> {
                pluralStringResource(
                    R.plurals.property_details_qt_bathrooms,
                    qtBathrooms,
                    qtBathrooms,
                )
            }
        },
        modifier = modifier
    )
}
