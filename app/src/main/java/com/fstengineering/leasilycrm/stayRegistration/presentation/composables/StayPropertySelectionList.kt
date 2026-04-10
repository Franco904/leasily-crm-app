package com.fstengineering.leasilycrm.stayRegistration.presentation.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.presentation.composables.cards.PropertyCard
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardUiState
import kotlinx.collections.immutable.ImmutableList

@Composable
fun StayPropertySelectionList(
    properties: ImmutableList<PropertyCardUiState>,
    onPropertySelect: (PropertyCardUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        itemsIndexed(
            items = properties,
            key = { _, property -> property.id },
            contentType = { _, entryType -> entryType },
        ) { _, property ->
            PropertyCard(
                property = property,
                onCardClick = { onPropertySelect(property) },
                hasMoreOptionsMenu = false,
                hasCharacteristicsChipsRow = false,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}
