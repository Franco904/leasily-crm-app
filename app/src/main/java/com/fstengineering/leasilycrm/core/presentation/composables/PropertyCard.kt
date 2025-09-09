package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.constants.APARTMENT_PROPERTY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.constants.DISTANCE_TO_SEA_PROXIMITY_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.models.Property
import kotlinx.collections.immutable.persistentListOf

@Composable
fun PropertyCard(
    property: Property,
    onEditProperty: () -> Unit,
    onDeleteProperty: () -> Unit,
    onViewPropertyDashboard: () -> Unit,
    onViewPropertyDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val mainPropertyPicture = remember(property) {
        property.files.find { picture -> picture.isMainPicture }
    }
    val numberText = if (property.number != null) ", ${property.number}" else ""

    AppCard(
        onClick = onViewPropertyDashboard,
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
                    .padding(16.dp)
            ) {
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
                        ),
                    )
                }
                Text(
                    text = "${property.streetAddress}$numberText",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (property.seaProximityType != null) {
                        CharacteristicReadOnlyChip(
                            characteristicText = when {
                                property.seaProximityType.id == DISTANCE_TO_SEA_PROXIMITY_TYPE_ID -> {
                                    stringResource(
                                        R.string.properties_sea_proximity_distance_to_sea,
                                        property.distanceToSea ?: "",
                                    )
                                }

                                else -> property.seaProximityType.name
                            }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    if (property.qtBedrooms > 0) {
                        CharacteristicReadOnlyChip(
                            characteristicText = stringResource(
                                R.string.properties_qt_bedrooms,
                                property.qtBedrooms,
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Card(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = when {
                                property.type?.id == APARTMENT_PROPERTY_TYPE_ID -> {
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
            }
        }
    }
}
