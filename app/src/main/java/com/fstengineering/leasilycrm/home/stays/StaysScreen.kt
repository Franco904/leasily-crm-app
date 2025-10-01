package com.fstengineering.leasilycrm.home.stays

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.StayTypeDialog

@Composable
fun StaysScreen(
    onNewStayBlank: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var mustShowStayTypeDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { mustShowStayTypeDialog = true },
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar_add_on),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
    }
    if (mustShowStayTypeDialog) {
        StayTypeDialog(
            onLoggedRealtorStayType = {
                mustShowStayTypeDialog = false
                onNewStayBlank(false)
            },
            onOtherRealtorStayType = {
                mustShowStayTypeDialog = false
                onNewStayBlank(true)
            },
            onCancel = { mustShowStayTypeDialog = false }
        )
    }
}
