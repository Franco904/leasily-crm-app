package com.fstengineering.leasilycrm.core.presentation.composables.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.constants.APARTMENT_PROPERTY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.constants.DISTANCE_TO_SEA_PROXIMITY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.constants.NOT_APPLICABLE_PROXIMITY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.composables.AppMoreOptionsMenu
import com.fstengineering.leasilycrm.core.presentation.composables.CharacteristicReadOnlyChip
import com.fstengineering.leasilycrm.core.presentation.composables.MoreOptionsMenuOption
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

    Box {
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
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    if (hasMoreOptionsMenu) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = property.neighborhood ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .weight(1f)
                            )
                            AppMoreOptionsMenu(
                                options = persistentListOf(
                                    MoreOptionsMenuOption(
                                        icon = Icons.Outlined.RemoveRedEye,
                                        text = stringResource(R.string.view_details_action_button_text),
                                        onClick = onViewPropertyDetails,
                                    ),
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
                                    MoreOptionsMenuOption(isHorizontalDivider = true),
                                    MoreOptionsMenuOption(
                                        iconDrawable = R.drawable.calendar_add_on,
                                        text = stringResource(R.string.new_property_stay_action_button_text),
                                        onClick = onNewStay,
                                    ),
                                ),
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${property.streetAddress}$numberText",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        if (hasCharacteristicsChipsRow) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CharacteristicsChipsRow(property = property)
                        }
                        if (hasViewDetailsButton) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = onViewPropertyDetails,
                                modifier = Modifier
                                    .align(Alignment.End)
                            ) {
                                Text(
                                    text = stringResource(R.string.view_details_action_button_text),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier.height(if (hasViewDetailsButton) 8.dp else 16.dp)
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
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
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            PropertyTypeIcon(propertyTypeId = property.type?.id)
                        }
                        if (hasCharacteristicsChipsRow) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CharacteristicsChipsRow(property = property)
                        }
                        if (hasViewDetailsButton) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = onViewPropertyDetails,
                                modifier = Modifier
                                    .align(Alignment.End)
                            ) {
                                Text(
                                    text = stringResource(R.string.view_details_action_button_text),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier.height(if (hasViewDetailsButton) 8.dp else 16.dp)
                        )
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
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val canShowSeaProximityChip = property.seaProximityType != null
                && property.seaProximityType.id != NOT_APPLICABLE_PROXIMITY_TYPE_ID

        if (canShowSeaProximityChip) {
            CharacteristicReadOnlyChip(
                characteristicText = when {
                    property.seaProximityType.id == DISTANCE_TO_SEA_PROXIMITY_TYPE_ID -> {
                        stringResource(
                            R.string.properties_sea_proximity_distance_to_sea,
                            property.distanceToSea ?: "",
                        )
                    }

                    else -> property.seaProximityType.name ?: ""
                }
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        if (property.qtBedrooms > 0) {
            CharacteristicReadOnlyChip(
                characteristicText = pluralStringResource(
                    R.plurals.properties_qt_bedrooms,
                    property.qtBedrooms,
                    property.qtBedrooms,
                ),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        PropertyTypeIcon(propertyTypeId = property.type?.id)
    }
}

@Composable
fun PropertyTypeIcon(
    propertyTypeId: Long?,
    modifier: Modifier = Modifier,
) {
    Card(
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        shape = CircleShape,
        modifier = modifier
            .size(36.dp)
    ) {
        Icon(
            imageVector = when {
                propertyTypeId == APARTMENT_PROPERTY_TYPE_ID -> {
                    Icons.Default.Apartment
                }

                else -> Icons.Default.House
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}
