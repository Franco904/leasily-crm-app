package com.fstengineering.leasilycrm.profile

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.external.interfaces.CredentialManagerAdapter
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent.ShowErrorBottomSheet
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent.ShowSnackBar
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.profile.ProfileViewModel.UiEvents.ContinueSignOut
import com.fstengineering.leasilycrm.profile.ProfileViewModel.UiEvents.EditUserSuccess
import com.fstengineering.leasilycrm.profile.ProfileViewModel.UiEvents.Error
import com.fstengineering.leasilycrm.profile.ProfileViewModel.UiEvents.SignOutSuccess
import com.fstengineering.leasilycrm.profile.composables.ConfirmSignOutUserDialog
import com.fstengineering.leasilycrm.profile.composables.EditCreciNumberDialog
import com.fstengineering.leasilycrm.profile.composables.ProfileDataSection
import com.fstengineering.leasilycrm.profile.composables.ProfileScreenTopBar
import com.fstengineering.leasilycrm.profile.models.strategies.SignOutErrorBottomSheetDataPropertiesStrategy
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

    val userUiState by viewModel.user.collectAsStateWithLifecycle()
    val creciFieldError by viewModel.creciFieldError.collectAsStateWithLifecycle()
    val isLoadingUser by viewModel.isLoadingUser.collectAsStateWithLifecycle()

    var isSigningOut by remember { mutableStateOf(false) }
    var isConfirmingSigningOut by remember { mutableStateOf(false) }
    var isEditingUser by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is Error -> {
                    isSigningOut = false

                    val errorData = uiEvent.error.toBottomSheetData(
                        SignOutErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(ShowErrorBottomSheet(errorData))
                }

                is EditUserSuccess -> {
                    onUiEvent(ShowSnackBar(uiEvent.messageId))
                }

                is ContinueSignOut -> {
                    launch {
                        credentialManager.clearGoogleCredentialState(activityContext = activity)
                            .onError { e ->
                                isSigningOut = false
                                onUiEvent(ShowErrorBottomSheet(e.toBottomSheetData()))
                            }
                            .onSuccess { viewModel.signOutUser() }
                    }
                }

                is SignOutSuccess -> {
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
            ProfileScreenTopBar(
                isSigningOut = isSigningOut,
                onBack = onBack,
            )
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
            if (isEditingUser) {
                EditCreciNumberDialog(
                    userUiState = userUiState,
                    onUserUiStateChange = viewModel::onCreciNumberChange,
                    creciNumberError = creciFieldError,
                    onEdit = { editedUser ->
                        isEditingUser = false

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

                        viewModel.onSignOut()
                    },
                    onCancel = { isConfirmingSigningOut = false },
                )
            }

            if (isLoadingUser) {
                AppCircularProgressIndicator(
                    modifier = Modifier
                        .size(64.dp)
                )
            } else {
                ProfileDataSection(
                    userUiState = userUiState,
                    isSigningOut = isSigningOut,
                    onSignOut = { isConfirmingSigningOut = true },
                    onEditUser = { if (!isEditingUser) isEditingUser = true }
                )
            }
        }
    }
}
