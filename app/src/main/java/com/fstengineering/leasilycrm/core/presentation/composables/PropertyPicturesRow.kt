package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.models.PropertyFileUiState
import kotlinx.collections.immutable.ImmutableList

@Composable
fun PropertyPicturesRow(
    pictures: ImmutableList<PropertyFileUiState>,
    modifier: Modifier = Modifier,
    onSelectMainPicture: ((Int, PropertyFileUiState) -> Unit)? = null,
    onDeletePicture: ((PropertyFileUiState) -> Unit)? = null,
    isReadOnly: Boolean = false,
) {
    LazyRow(
        modifier = modifier
    ) {
        itemsIndexed(
            items = pictures,
            key = { i, file -> file.id ?: "${file.uri}$i" },
        ) { i, file ->
            PropertyPictureItem(
                picture = file,
                mustShowMainPictureIndicator = !isReadOnly && file.isMainPicture && pictures.size > 1,
                isReadOnly = isReadOnly,
                onClick = { onSelectMainPicture?.invoke(i, file) },
                onDeletePicture = { onDeletePicture?.invoke(file) },
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun PropertyPictureItem(
    picture: PropertyFileUiState,
    mustShowMainPictureIndicator: Boolean,
    isReadOnly: Boolean,
    onClick: () -> Unit,
    onDeletePicture: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Box {
            Card(
                border = BorderStroke(
                    width = if (mustShowMainPictureIndicator) 2.dp else 1.dp,
                    color = if (mustShowMainPictureIndicator) {
                        MaterialTheme.colorScheme.tertiary
                    } else MaterialTheme.colorScheme.outlineVariant,
                ),
                shape = MaterialTheme.shapes.small,
                onClick = onClick,
                modifier = modifier
                    .size(96.dp),
            ) {
                AsyncImage(
                    model = picture.uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }
            if (!isReadOnly) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = CircleShape,
                    onClick = onDeletePicture,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = (-8).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .size(8.dp)
                    )
                }
            }
        }
        if (mustShowMainPictureIndicator) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.property_registration_pictures_main_picture_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
