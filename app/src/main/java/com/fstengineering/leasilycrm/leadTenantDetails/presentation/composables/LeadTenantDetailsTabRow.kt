package com.fstengineering.leasilycrm.leadTenantDetails.presentation.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.fstengineering.leasilycrm.leadTenantDetails.models.LeadTenantDetailsTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadTenantDetailsTabRow(
    selectedTabId: Int,
    onTabSelect: (Int) -> Unit,
    hasHistoryTab: Boolean,
    modifier: Modifier = Modifier,
) {
    PrimaryTabRow(
        selectedTabIndex = selectedTabId - 1,
        modifier = modifier
    ) {
        LeadTenantDetailsTab.entries
            .filter {
                if (it == LeadTenantDetailsTab.History) hasHistoryTab
                else true
            }
            .forEach { tab ->
                val isTabSelected = selectedTabId == tab.id

                Tab(
                    selected = isTabSelected,
                    onClick = { onTabSelect(tab.id) },
                    text = {
                        Text(
                            text = stringResource(tab.labelId),
                            style = MaterialTheme.typography.titleSmall,
                            color = when {
                                isTabSelected -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )
            }
    }
}
