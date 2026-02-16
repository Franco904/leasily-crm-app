package com.fstengineering.leasilycrm.core.presentation.composables.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.constants.APARTMENT_PROPERTY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.constants.DISTANCE_TO_SEA_PROXIMITY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.constants.NOT_APPLICABLE_PROXIMITY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsIconDropdownMenu
import com.fstengineering.leasilycrm.core.presentation.composables.CharacteristicReadOnlyChip
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.AppPrimaryIconButton
import com.fstengineering.leasilycrm.core.presentation.models.MoreOptionsMenuOption
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PropertyCard(
    property: Property,
    modifier: Modifier = Modifier,
    onCardClick: (() -> Unit)? = null,
    onViewPropertyDetails: () -> Unit = {},
    onEditProperty: () -> Unit = {},
    onDeleteProperty: () -> Unit = {},
    onNewStay: () -> Unit = {},
    hasMoreOptionsMenu: Boolean = true,
    hasCharacteristicsChipsRow: Boolean = true,
    hasViewDetailsButton: Boolean = false,
    isSelected: Boolean = false,
) {
    val mainPropertyPicture = remember(property) {
        property.files.find { picture -> picture.isMainPicture }
    }
    val numberText = if (property.number != null) ", ${property.number}" else ""

    AppCard(
        onClick = onCardClick,
        isSelected = isSelected,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column {
            if (mainPropertyPicture != null) {
                Card(
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    AsyncImage(
                        model = mainPropertyPicture.uriString!!.toUri(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = property.neighborhood ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "${property.streetAddress}$numberText",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (hasCharacteristicsChipsRow) {
                        Spacer(modifier = Modifier.height(12.dp))
                        CharacteristicsChipsRow(property = property)
                    }
                }

                if (hasMoreOptionsMenu) {
                    AppMoreOptionsIconDropdownMenu(
                        options = persistentListOf(
                            MoreOptionsMenuOption(
                                icon = Icons.Outlined.Edit,
                                text = stringResource(R.string.edit_action_button_text),
                                onClick = onEditProperty,
                            ),
                            MoreOptionsMenuOption(
                                icon = Icons.Outlined.Delete,
                                text = stringResource(R.string.delete_action_button_text),
                                onClick = onDeleteProperty,
                            ),
                        ),
                        modifier = Modifier
                            .padding(top = 16.dp, end = 16.dp)
                            .align(Alignment.TopEnd)
                    )
                }

                if (hasCharacteristicsChipsRow) {
                    AppPrimaryIconButton(
                        icon = painterResource(R.drawable.calendar_add_on),
                        onClick = onNewStay,
                        modifier = Modifier
                            .padding(bottom = 8.dp, end = 8.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            if (hasViewDetailsButton) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                ) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = onViewPropertyDetails,
                        ) {
                            Text(
                                text = stringResource(R.string.view_details_action_button_text),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharacteristicsChipsRow(
    property: Property,
    modifier: Modifier = Modifier,
) {
    val canShowSeaProximityChip = property.seaProximityType != null
            && property.seaProximityType.id != NOT_APPLICABLE_PROXIMITY_TYPE_ID

    Row(
        modifier = modifier
    ) {
        CharacteristicReadOnlyChip(
            prefixIcon = when {
                property.type?.id == APARTMENT_PROPERTY_TYPE_ID -> Icons.Default.Apartment
                else -> Icons.Default.House
            },
            characteristicText = when {
                property.type?.id == APARTMENT_PROPERTY_TYPE_ID -> stringResource(R.string.apartment_chip_text)
                else -> stringResource(R.string.house_chip_text)
            },
        )
        if (canShowSeaProximityChip) {
            Spacer(modifier = Modifier.width(4.dp))
            CharacteristicReadOnlyChip(
                characteristicText = run {
                    val text = stringResource(
                        R.string.properties_sea_proximity_distance_to_sea,
                        property.distanceToSea ?: "",
                    )

                    when {
                        property.seaProximityType.id == DISTANCE_TO_SEA_PROXIMITY_TYPE_ID -> text
                        else -> property.seaProximityType.name ?: ""
                    }
                }
            )
        }
        if (property.bedrooms > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            CharacteristicReadOnlyChip(
                characteristicText = pluralStringResource(
                    R.plurals.properties_qt_bedrooms,
                    property.bedrooms,
                    property.bedrooms,
                ),
            )
        }
    }
}
