package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppComboBottomSheet(
    sheetState: SheetState,
    options: ImmutableList<String>,
    onOptionSelect: (String) -> Unit,
    onDismissRequest: () -> Unit,
    emptyOptionsIcon: ImageVector,
    emptyOptionsText: String,
    modifier: Modifier = Modifier,
    emptyAction: (@Composable () -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        if (options.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .navigationBarsPadding()
            ) {
                Icon(
                    imageVector = emptyOptionsIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = emptyOptionsText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (emptyAction != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    emptyAction()
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .navigationBarsPadding()
            ) {
                itemsIndexed(
                    items = options,
                    key = { i, option -> "$option#$i" },
                ) { i, option ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOptionSelect(option)

                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) onDismissRequest()
                                }
                            }
                    ) {
                        if (i == 0) HorizontalDivider()
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(16.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
