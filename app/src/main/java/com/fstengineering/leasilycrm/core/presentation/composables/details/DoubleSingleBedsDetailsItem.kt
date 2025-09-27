package com.fstengineering.leasilycrm.core.presentation.composables.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun DoubleSingleBedsDetailsItem(
    qtDoubleBeds: Int,
    qtSingleBeds: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val singleBeds = remember(qtSingleBeds) {
        when {
            qtSingleBeds == 0 -> context.getString(R.string.property_details_no_single_beds)
                .lowercase()

            else -> context.resources.getQuantityString(
                R.plurals.property_details_qt_single_beds,
                qtSingleBeds,
                qtSingleBeds,
            )
        }
    }

    IconValueDetailsItem(
        icon = Icons.Outlined.Bed,
        value = when {
            qtDoubleBeds == 0 && qtSingleBeds == 0 -> {
                stringResource(R.string.property_details_no_beds)
            }

            qtDoubleBeds == 0 -> {
                stringResource(R.string.property_details_no_double_beds, singleBeds)
            }

            else -> {
                pluralStringResource(
                    id = R.plurals.property_details_qt_double_beds,
                    count = qtDoubleBeds,
                    qtDoubleBeds,
                    singleBeds,
                )
            }
        },
        modifier = modifier
    )
}
