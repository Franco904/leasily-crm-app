package com.fstengineering.leasilycrm.stayRegistration.presentation.composables.fields

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.entities.StayFile
import com.fstengineering.leasilycrm.core.presentation.composables.cards.DocumentFileCard
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate

fun LazyListScope.stayDocumentsFormField(
    stayForm: Stay,
    onStayFormChange: (Stay) -> Unit,
    stayNotDeletedFiles: PersistentList<StayFile>,
    onAddStayFile: () -> Unit,
) {
    item {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.stay_registration_documents_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
            )
            TextButton(onClick = onAddStayFile) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.add_action_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
    item { Spacer(modifier = Modifier.height(16.dp)) }
    item {
        Text(
            text = stringResource(R.string.stay_registration_documents_body_text),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    item { Spacer(modifier = Modifier.height(16.dp)) }
    itemsIndexed(
        items = stayNotDeletedFiles,
        key = { i, file -> file.id ?: "${file.name}$i" },
    ) { i, file ->
        DocumentFileCard(
            fileName = file.name,
            actionIcon = Icons.Filled.Close,
            onAction = {
                val newFiles = if (file.id == null) {
                    stayNotDeletedFiles.remove(file)
                } else {
                    stayNotDeletedFiles.mutate {
                        it[i] = file.copy(isDeleted = true)
                    }
                }

                onStayFormChange(
                    stayForm.copy(files = newFiles),
                )
            },
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
