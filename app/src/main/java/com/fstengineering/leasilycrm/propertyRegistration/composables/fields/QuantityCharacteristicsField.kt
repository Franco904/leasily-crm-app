package com.fstengineering.leasilycrm.propertyRegistration.composables.fields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.presentation.composables.QuantityAnswerCounter

@Composable
fun QuantityCharacteristicsField(
    propertyForm: Property,
    onPropertyFormChange: (Property) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .padding(start = 16.dp, end = 24.dp)
        ) {
            QuantityAnswerCounter(
                label = stringResource(R.string.property_registration_bedrooms_body_text),
                count = propertyForm.qtBedrooms,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtBedrooms = it)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            QuantityAnswerCounter(
                label = stringResource(R.string.property_registration_en_suites_body_text),
                count = propertyForm.qtEnSuites,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtEnSuites = it)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            QuantityAnswerCounter(
                label = stringResource(R.string.property_registration_double_beds_body_text),
                count = propertyForm.qtDoubleBeds,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtDoubleBeds = it)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            QuantityAnswerCounter(
                label = stringResource(R.string.property_registration_single_beds_body_text),
                count = propertyForm.qtSingleBeds,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtSingleBeds = it)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            QuantityAnswerCounter(
                label = stringResource(R.string.property_registration_bathrooms_body_text),
                count = propertyForm.qtBathrooms,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtBathrooms = it)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            QuantityAnswerCounter(
                label = stringResource(R.string.property_registration_parking_spots_body_text),
                count = propertyForm.qtParkingSpots,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtParkingSpots = it)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            QuantityAnswerCounter(
                label = stringResource(R.string.property_registration_air_conditioners_body_text),
                count = propertyForm.qtAirConditioners,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtAirConditioners = it)) },
            )
            Spacer(modifier = Modifier.height(16.dp))
            QuantityAnswerCounter(
                label = stringResource(R.string.property_registration_max_guests_body_text),
                count = propertyForm.qtMaxGuests,
                onCountChange = { onPropertyFormChange(propertyForm.copy(qtMaxGuests = it)) },
            )
        }
    }
}
