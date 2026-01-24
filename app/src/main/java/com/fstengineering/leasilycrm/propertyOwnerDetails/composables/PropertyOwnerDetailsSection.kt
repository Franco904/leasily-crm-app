package com.fstengineering.leasilycrm.propertyOwnerDetails.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.presentation.composables.cards.DocumentFileCard
import com.fstengineering.leasilycrm.core.presentation.composables.details.IconLabelValueDetailsItem

@Composable
fun PropertyOwnerDetailsSection(
    propertyOwner: PropertyOwner,
    onOpenPropertyOwnerFile: (PropertyOwnerFile) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            IconLabelValueDetailsItem(
                icon = Icons.Outlined.Person,
                label = stringResource(R.string.property_owner_registration_name_label),
                value = propertyOwner.name
                    ?: stringResource(R.string.not_registered_info_body_text),
                isEmptyText = propertyOwner.name == null,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            IconLabelValueDetailsItem(
                icon = Icons.Outlined.Phone,
                label = stringResource(R.string.property_owner_registration_phone_label),
                value = propertyOwner.phone
                    ?: stringResource(R.string.not_registered_info_body_text),
                isEmptyText = propertyOwner.phone == null,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            IconLabelValueDetailsItem(
                icon = Icons.Outlined.Email,
                label = stringResource(R.string.property_owner_registration_email_label),
                value = propertyOwner.email
                    ?: stringResource(R.string.not_registered_info_body_text),
                isEmptyText = propertyOwner.email == null,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            IconLabelValueDetailsItem(
                icon = Icons.Outlined.Payment,
                label = stringResource(R.string.property_owner_registration_payment_key_label),
                value = propertyOwner.paymentKey
                    ?: stringResource(R.string.not_registered_info_body_text),
                isEmptyText = propertyOwner.paymentKey == null,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.property_owner_registration_documents_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(if (propertyOwner.files.isEmpty()) 16.dp else 24.dp))
        }
        if (propertyOwner.files.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_file_registered_body_text),
                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            itemsIndexed(
                items = propertyOwner.files,
                key = { i, file -> file.id ?: "${file.name}$i" },
            ) { _, file ->
                DocumentFileCard(
                    fileName = file.name,
                    actionIcon = Icons.AutoMirrored.Outlined.OpenInNew,
                    onAction = { onOpenPropertyOwnerFile(file) },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
