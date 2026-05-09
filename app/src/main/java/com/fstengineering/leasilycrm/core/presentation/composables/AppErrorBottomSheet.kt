package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.AppOutlinedButton
import com.fstengineering.leasilycrm.core.presentation.composables.buttons.AppPrimaryButton
import com.fstengineering.leasilycrm.core.presentation.models.ErrorBottomSheetData
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppErrorBottomSheet(
    sheetState: SheetState,
    errorBottomSheetData: ErrorBottomSheetData,
    onDismissRequest: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = stringResource(errorBottomSheetData.title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(errorBottomSheetData.description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppPrimaryButton(
                label = stringResource(R.string.understand_button_text),
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
            )
            if (errorBottomSheetData.secondaryActionLabel != null && errorBottomSheetData.onSecondaryAction != null) {
                Spacer(modifier = Modifier.height(4.dp))
                AppOutlinedButton(
                    label = stringResource(errorBottomSheetData.secondaryActionLabel),
                    onClick = {
                        onDismissRequest()
                        errorBottomSheetData.onSecondaryAction(context) { a -> onUiEvent(a) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun AppErrorBottomSheetPreview(
    modifier: Modifier = Modifier
) {
    AppTheme {
        AppErrorBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            errorBottomSheetData = ErrorBottomSheetData(
                title = R.string.unknown_error_title,
                description = R.string.unknown_error_description_default,
                secondaryActionLabel = null,
                onSecondaryAction = null,
            ),
            onDismissRequest = {},
            onUiEvent = {},
            modifier = modifier
        )
    }
}
