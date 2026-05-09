package com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.presentation.composables.AppCard
import com.fstengineering.leasilycrm.core.presentation.utils.itemsIndexed
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.models.LeadTenantHistoryItemUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

@Composable
fun LeadTenantDetailsHistoryPage(
    historyByYear: ImmutableMap<Int, ImmutableList<LeadTenantHistoryItemUiState>>,
    onViewHistoryItem: (id: Long, isAssistance: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    val currentYear = remember { AppDate.now().year }

    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .fillMaxSize()
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        itemsIndexed(
            items = historyByYear,
            key = { _, (year, _) -> year },
            contentType = { _, entryType -> entryType },
        ) { i, (year, yearHistoryItems) ->
            val yearLabel =
                if (year == currentYear) stringResource(R.string.this_year_label)
                else year.toString()

            if (i > 0) Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = yearLabel,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                yearHistoryItems.forEachIndexed { i, historyItem ->
                    val labelStringId =
                        if (historyItem.isAssistance) R.string.lead_tenant_history_details_assistance_at_label
                        else R.string.lead_tenant_history_details_stay_at_label

                    if (i > 0) Spacer(modifier = Modifier.height(8.dp))
                    LeadTenantDetailsHistoryCard(
                        label = stringResource(
                            labelStringId,
                            historyItem.dateTime.dayOfMonth,
                            historyItem.dateTime.month,
                            historyItem.dateTime.year,
                        ),
                        onViewHistoryItem = {
                            onViewHistoryItem(historyItem.id, historyItem.isAssistance)
                        },
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
fun LeadTenantDetailsHistoryCard(
    label: String,
    onViewHistoryItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(
        onClick = { onViewHistoryItem() },
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}
