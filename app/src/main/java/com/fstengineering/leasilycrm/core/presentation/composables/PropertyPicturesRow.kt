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
    propertyNotDeletedPictures: PersistentList<PropertyFile>,
    modifier: Modifier = Modifier,
    propertyForm: Property? = null,
    onPropertyFormChange: ((Property) -> Unit)? = null,
    onViewPropertyPicture: ((PropertyFile) -> Unit)? = null,
    isReadOnly: Boolean = false,
) {
    LazyRow(
        modifier = modifier
    ) {
        itemsIndexed(
            items = propertyNotDeletedPictures,
            key = { i, file -> file.id ?: "${file.uriString}$i" },
        ) { i, file ->
            if (file.uriString == null) return@itemsIndexed

            PropertyPictureItem(
                propertyPicture = file,
                isMainPicture = !isReadOnly && file.isMainPicture && propertyNotDeletedPictures.size > 1,
                isReadOnly = isReadOnly,
                onClick = if (isReadOnly) {
                    { onViewPropertyPicture?.invoke(file) }
                } else {
                    {
                        if (file.isMainPicture || propertyNotDeletedPictures.size == 1) return@PropertyPictureItem

                        val newFiles = propertyNotDeletedPictures
                            .map { file -> file.copy(isMainPicture = false) }
                            .toPersistentList()
                            .mutate { it[i] = file.copy(isMainPicture = true) }

                        if (propertyForm == null) return@PropertyPictureItem
                        onPropertyFormChange?.invoke(propertyForm.copy(files = newFiles))
                    }
                },
                onDeletePicture = if (isReadOnly) {
                    {}
                } else {
                    {
                        val newFiles = if (file.id == null) {
                            propertyNotDeletedPictures.remove(file)
                        } else {
                            propertyNotDeletedPictures.mutate {
                                it[i] = file.copy(isDeleted = true)
                            }
                        }

                        val hasMainPicture = newFiles.any { !it.isDeleted && it.isMainPicture }

                        val newFilesUpdated = if (newFiles.isNotEmpty() && !hasMainPicture) {
                            newFiles.mutate { it[0] = it[0].copy(isMainPicture = true) }
                        } else newFiles

                        if (propertyForm == null) return@PropertyPictureItem
                        onPropertyFormChange?.invoke(propertyForm.copy(files = newFilesUpdated))
                    }
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
                    width = if (isMainPicture) 2.dp else 1.dp,
                    color = if (isMainPicture) {
                        MaterialTheme.colorScheme.tertiary
                    } else MaterialTheme.colorScheme.outlineVariant,
                ),
                shape = MaterialTheme.shapes.small,
                onClick = onClick,
                modifier = modifier
                    .size(96.dp),
            ) {
                AsyncImage(
                    model = propertyPicture.uriString!!.toUri(),
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
        if (isMainPicture) {
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
