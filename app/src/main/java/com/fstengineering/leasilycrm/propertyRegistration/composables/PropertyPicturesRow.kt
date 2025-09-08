package com.fstengineering.leasilycrm.propertyRegistration.composables

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
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.PropertyFile
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList

@Composable
fun PropertyPicturesRow(
    propertyNotDeletedFiles: PersistentList<PropertyFile>,
    propertyForm: Property,
    onPropertyFormChange: (Property) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier
    ) {
        itemsIndexed(
            items = propertyNotDeletedFiles,
            key = { i, file -> file.id ?: "${file.uriString}$i" },
        ) { i, file ->
            if (file.uriString == null) return@itemsIndexed

            PropertyPictureItem(
                propertyPicture = file,
                isMainPicture = file.isMainPicture && propertyNotDeletedFiles.size > 1,
                onSetMainPicture = {
                    if (file.isMainPicture || propertyNotDeletedFiles.size == 1) return@PropertyPictureItem

                    val newFiles = propertyNotDeletedFiles
                        .map { file -> file.copy(isMainPicture = false) }
                        .toPersistentList()
                        .mutate { it[i] = file.copy(isMainPicture = true) }

                    onPropertyFormChange(propertyForm.copy(files = newFiles))
                },
                onDeletePicture = {
                    val newFiles = if (file.id == null) {
                        propertyNotDeletedFiles.remove(file)
                    } else {
                        propertyNotDeletedFiles.mutate {
                            it[i] = file.copy(isDeleted = true)
                        }
                    }

                    onPropertyFormChange(propertyForm.copy(files = newFiles))
                },
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun PropertyPictureItem(
    propertyPicture: PropertyFile,
    isMainPicture: Boolean,
    onSetMainPicture: () -> Unit,
    onDeletePicture: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Box {
            Card(
                border = BorderStroke(
                    width = if (isMainPicture) 2.dp else 1.dp,
                    color = if (isMainPicture) {
                        MaterialTheme.colorScheme.tertiary
                    } else MaterialTheme.colorScheme.outlineVariant,
                ),
                shape = MaterialTheme.shapes.small,
                onClick = onSetMainPicture,
                modifier = modifier
                    .size(96.dp),
            ) {
                AsyncImage(
                    model = propertyPicture.uriString!!.toUri(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }
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
        Spacer(modifier = Modifier.height(4.dp))
        if (isMainPicture) {
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
