package com.fstengineering.leasilycrm.propertyDetails.composables.items

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AirlineSeatIndividualSuite
import androidx.compose.material.icons.outlined.Bathtub
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Property

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
            qtBedrooms = property.qtBedrooms,
            qtEnSuites = property.qtEnSuites,
        )
        Spacer(modifier = Modifier.height(16.dp))
        DoubleSingleBedsDetailsItem(
            qtDoubleBeds = property.qtDoubleBeds,
            qtSingleBeds = property.qtSingleBeds,
        )
        Spacer(modifier = Modifier.height(16.dp))
        BathroomsDetailsItem(qtBathrooms = property.qtBathrooms)
        Spacer(modifier = Modifier.height(16.dp))
        ParkingSpotsDetailsItem(qtParkingSpots = property.qtParkingSpots)
        Spacer(modifier = Modifier.height(16.dp))
        AirConditionersDetailsItem(qtAirConditioners = property.qtAirConditioners)
        Spacer(modifier = Modifier.height(16.dp))
        MaxGuestsDetailsItem(qtMaxGuests = property.qtMaxGuests)
    }
}

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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.AirlineSeatIndividualSuite,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = when {
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
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Bed,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = when {
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
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun BathroomsDetailsItem(
    qtBathrooms: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Bathtub,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = when {
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
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun ParkingSpotsDetailsItem(
    qtParkingSpots: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.DirectionsCar,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = when {
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
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun AirConditionersDetailsItem(
    qtAirConditioners: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.airwave),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = when {
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
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun MaxGuestsDetailsItem(
    qtMaxGuests: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.FamilyRestroom,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = when {
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
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
