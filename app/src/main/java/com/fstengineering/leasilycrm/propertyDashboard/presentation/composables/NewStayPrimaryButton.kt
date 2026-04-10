package com.fstengineering.leasilycrm.propertyDashboard.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.AppPrimaryButton

@Composable
fun NewStayPrimaryButton(
    onNewStay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppPrimaryButton(
        prefixIconDrawable = R.drawable.calendar_add_on,
        label = stringResource(R.string.new_property_stay_action_button_text),
        onClick = onNewStay,
        modifier = modifier
    )
}
