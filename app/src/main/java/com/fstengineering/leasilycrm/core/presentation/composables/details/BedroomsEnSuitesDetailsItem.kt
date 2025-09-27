package com.fstengineering.leasilycrm.core.presentation.composables.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AirlineSeatIndividualSuite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun BedroomsEnSuitesDetailsItem(
    qtBedrooms: Int,
    qtEnSuites: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val enSuites = remember(qtEnSuites) {
        when {
            qtEnSuites == 0 -> context.getString(R.string.property_details_no_en_suites)
            else -> context.resources.getQuantityString(
                R.plurals.property_details_qt_en_suites,
                qtEnSuites,
                qtEnSuites,
            )
        }
    }

    IconValueDetailsItem(
        icon = Icons.Outlined.AirlineSeatIndividualSuite,
        value = when {
            qtBedrooms == 0 -> {
                stringResource(R.string.property_details_no_bedrooms)
            }

            else -> {
                pluralStringResource(
                    id = R.plurals.property_details_qt_bedrooms,
                    count = qtBedrooms,
                    qtBedrooms,
                    enSuites,
                )
            }
        },
        modifier = modifier
    )
}
