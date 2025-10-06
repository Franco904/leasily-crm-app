package com.fstengineering.leasilycrm.propertyDashboard.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R

@Composable
fun ViewAllStaysTextButton(
    onViewNextStays: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onViewNextStays,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.view_all_a_action_button_text),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
