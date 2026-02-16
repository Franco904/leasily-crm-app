package com.fstengineering.leasilycrm.propertyDetails.composables.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.presentation.composables.details.BathroomsDetailsItem
import com.fstengineering.leasilycrm.core.presentation.composables.details.BedroomsEnSuitesDetailsItem
import com.fstengineering.leasilycrm.core.presentation.composables.details.DoubleSingleBedsDetailsItem
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconValueDetailsItem
import com.fstengineering.leasilycrm.core.presentation.composables.details.ParkingSpotsDetailsItem

@Composable
fun PropertyQuantityCharacteristicsDetailsItems(
    property: Property,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.property_details_basic_characteristics_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        BedroomsEnSuitesDetailsItem(
            qtBedrooms = property.bedrooms,
            qtEnSuites = property.enSuites,
        )
        Spacer(modifier = Modifier.height(16.dp))
        DoubleSingleBedsDetailsItem(
            qtDoubleBeds = property.doubleBeds,
            qtSingleBeds = property.singleBeds,
        )
        Spacer(modifier = Modifier.height(16.dp))
        BathroomsDetailsItem(qtBathrooms = property.bathrooms)
        Spacer(modifier = Modifier.height(16.dp))
        ParkingSpotsDetailsItem(qtParkingSpots = property.parkingSpots)
        Spacer(modifier = Modifier.height(16.dp))
        AirConditionersDetailsItem(qtAirConditioners = property.airConditioners)
        Spacer(modifier = Modifier.height(16.dp))
        MaxGuestsDetailsItem(qtMaxGuests = property.maxGuests)
    }
}

@Composable
fun AirConditionersDetailsItem(
    qtAirConditioners: Int,
    modifier: Modifier = Modifier,
) {
    IconValueDetailsItem(
        iconDrawable = R.drawable.airwave,
        value = when {
            qtAirConditioners == 0 -> {
                stringResource(R.string.property_details_no_air_conditioners)
            }

            else -> {
                pluralStringResource(
                    R.plurals.property_details_qt_air_conditioners,
                    qtAirConditioners,
                    qtAirConditioners,
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun MaxGuestsDetailsItem(
    qtMaxGuests: Int,
    modifier: Modifier = Modifier,
) {
    IconValueDetailsItem(
        icon = Icons.Outlined.FamilyRestroom,
        value = when {
            qtMaxGuests == 0 -> {
                stringResource(R.string.property_details_no_max_guests)
            }

            else -> {
                pluralStringResource(
                    R.plurals.property_details_qt_max_guests,
                    qtMaxGuests,
                    qtMaxGuests,
                )
            }
        },
        modifier = modifier
    )
}
