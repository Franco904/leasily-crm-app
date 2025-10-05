package com.fstengineering.leasilycrm.stayDetails.composables.items

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.StayFile
import com.fstengineering.leasilycrm.core.presentation.composables.cards.DocumentFileCard

fun LazyListScope.stayDocumentsDetailsItem(
    files: List<StayFile>,
    onOpenStayFile: (StayFile) -> Unit,
) {
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
                text = stringResource(R.string.stay_registration_documents_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
    item {
        Spacer(modifier = Modifier.height(if (files.isEmpty()) 16.dp else 24.dp))
    }
    if (files.isEmpty()) {
        item {
            Text(
                text = stringResource(R.string.no_file_registered_body_text),
                style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        itemsIndexed(
            items = files,
            key = { i, file -> file.id ?: "${file.name}$i" },
        ) { _, file ->
            DocumentFileCard(
                fileName = file.name,
                actionIcon = Icons.AutoMirrored.Outlined.OpenInNew,
                onAction = { onOpenStayFile(file) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
