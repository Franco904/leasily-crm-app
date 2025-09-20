package com.fstengineering.leasilycrm.assistanceRegistration.composables.fields

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.NeighborhoodFormField
import kotlinx.collections.immutable.ImmutableList

fun LazyListScope.searchedNeighborhoodsFormField(
    searchedNeighborhoodInputs: ImmutableList<AssistanceSearchedNeighborhood>,
    onSearchedNeighborhoodInput: (AssistanceSearchedNeighborhood, Int) -> Unit,
    onAddNewNeighborhood: () -> Unit,
    onRemoveNeighborhood: (AssistanceSearchedNeighborhood) -> Unit,
    pastNeighborhoods: ImmutableList<String>,
) {
    itemsIndexed(
        items = searchedNeighborhoodInputs,
        key = { i, entry -> entry.id ?: "${entry.id}$i" },
        contentType = { _, entryType -> entryType },
    ) { i, assistanceSearchedNeighborhoodInput ->
        if (i > 0) Spacer(modifier = Modifier.height(16.dp))
        NeighborhoodFormField(
            value = assistanceSearchedNeighborhoodInput.neighborhood,
            onValueChange = { newNeighborhoodName ->
                val updatedSearchedNeighborhoodInput = assistanceSearchedNeighborhoodInput
                    .copy(neighborhood = newNeighborhoodName)

                onSearchedNeighborhoodInput(updatedSearchedNeighborhoodInput, i)
            },
            pastNeighborhoods = pastNeighborhoods,
            hasSupportingText = false,
            hasClearButton = i > 0,
            onClearValue = { onRemoveNeighborhood(assistanceSearchedNeighborhoodInput) },
        )
    }
    item { Spacer(modifier = Modifier.height(8.dp)) }
    item {
        TextButton(onClick = onAddNewNeighborhood) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.assistance_registration_add_preferred_neighborhood_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
