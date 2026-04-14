package com.fstengineering.leasilycrm.stayDetails.presentation.composables.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconValueDetailsItem

@Composable
fun StayQtGuestsDetailsItem(
    qtGuests: Int?,
    modifier: Modifier = Modifier,
) {
    IconValueDetailsItem(
        icon = Icons.Outlined.FamilyRestroom,
        value = when {
            qtGuests != null -> {
                pluralStringResource(
                    R.plurals.stay_details_qt_guests,
                    qtGuests,
                    qtGuests,
                )
            }

            else -> stringResource(R.string.not_registered_info_body_text)
        },
        isEmptyText = qtGuests == null,
        modifier = modifier
    )
}
