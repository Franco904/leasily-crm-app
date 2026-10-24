package com.fstengineering.leasilycrm.profile.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.CredentialManagerAdapter
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent.ShowErrorBottomSheet
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent.ShowSnackBar
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.profile.presentation.composables.ConfirmSignOutUserDialog
import com.fstengineering.leasilycrm.profile.presentation.composables.EditCreciNumberDialog
import com.fstengineering.leasilycrm.profile.presentation.composables.ProfileDataSection
import com.fstengineering.leasilycrm.profile.presentation.composables.ProfileScreenTopBar
import com.fstengineering.leasilycrm.profile.presentation.models.ProfileUiState
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    credentialManager: CredentialManagerAdapter,
    onBack: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val creciFieldError by viewModel.creciFieldError.collectAsStateWithLifecycle()

    var isSigningOut by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var isConfirmingSigningOut by remember { mutableStateOf(false) }
    var isEditingUser by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is ProfileViewModel.UiEvent.BottomSheetError -> {
                    isSigningOut = false

                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(ShowErrorBottomSheet(errorData))
                }

                is ProfileViewModel.UiEvent.EditUserSuccess -> {
                    onUiEvent(ShowSnackBar(uiEvent.messageId))
                }

                is ProfileViewModel.UiEvent.SignOutSuccess -> {
                    isSigningOut = false
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        topBar = {
            Column {
                ProfileScreenTopBar(
                    isSigningOut = isSigningOut,
                    onBack = onBack,
                )
                HorizontalDivider()
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 32.dp,
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 32.dp,
                    bottom = innerPadding.calculateBottomPadding(),
                )
        ) {
            when (uiState) {
                is ProfileUiState.Loading -> {
                    AppCircularProgressIndicator(
                        modifier = Modifier
                            .size(64.dp)
                    )
                }

                is ProfileUiState.Loaded -> {
                    ProfileDataSection(
                        profileUiState = uiState as ProfileUiState.Loaded,
                        isSigningOut = isSigningOut,
                        onSignOut = { isConfirmingSigningOut = true },
                        onEditUser = { if (!isEditingUser) isEditingUser = true }
                    )
                }
            }

            if (isEditingUser && uiState is ProfileUiState.Loaded) {
                val loadedState = (uiState as ProfileUiState.Loaded)
                val creciNumber = loadedState.creciNumber

                EditCreciNumberDialog(
                    initialCreciNumber = creciNumber,
                    onCreciNumberChange = viewModel::onCreciNumberChange,
                    creciNumberError = creciFieldError,
                    onCreciFocusLoss = viewModel::onCreciFocusLoss,
                    onEdit = { creciNumber ->
                        isEditingUser = false

                        val editedUser = loadedState.copy(creciNumber = creciNumber)

                        viewModel.editUser(editedUser)
                    },
                    onCancel = {
                        isEditingUser = false

                        viewModel.clearErrors()
                    },
                )
            }

            if (isConfirmingSigningOut) {
                ConfirmSignOutUserDialog(
                    onConfirmSignOut = {
                        isConfirmingSigningOut = false
                        isSigningOut = true

                        coroutineScope.launch {
                            credentialManager.clearGoogleCredentialState(activityContext = activity)
                                .onError { e ->
                                    isSigningOut = false
                                    onUiEvent(ShowErrorBottomSheet(e.toBottomSheetData()))
                                }
                                .onSuccess { viewModel.signOutUser() }
                        }
                    },
                    onCancel = { isConfirmingSigningOut = false },
                )
            }
        }
    }
}
