package com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.presentation.composables.cards.ImovelCard
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import kotlinx.collections.immutable.ImmutableList

@Composable
fun OccupiedPeriodImovelSelectionList(
    imoveis: ImmutableList<ImovelCardUiState>,
    onImovelSelect: (ImovelCardUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        itemsIndexed(
            items = imoveis,
            key = { _, imovel -> imovel.id },
            contentType = { _, entryType -> entryType },
        ) { _, imovel ->
            ImovelCard(
                imovel = imovel,
                onCardClick = { onImovelSelect(imovel) },
                hasMoreOptionsMenu = false,
                hasCharacteristicsChipsRow = false,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}
