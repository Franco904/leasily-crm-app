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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.core.application.theme.AppTheme
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.external.interfaces.CredentialManagerAdapter
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.profile.ProfileViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.profile.ProfileViewModel.UiEvents.EditUserSuccess
import com.fstengineering.leasilycrm.profile.ProfileViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.profile.composables.ConfirmSignOutUserDialog
import com.fstengineering.leasilycrm.profile.composables.EditUserDialog
import com.fstengineering.leasilycrm.profile.composables.ProfileDataSection
import com.fstengineering.leasilycrm.profile.composables.ProfileScreenTopBar
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    credentialManager: CredentialManagerAdapter,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onSignOut: () -> Unit = {},
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val coroutineScope = rememberCoroutineScope()

    val surfaceColor = MaterialTheme.colorScheme.surface

    val userUiState by viewModel.user.collectAsStateWithLifecycle()
    val editUserFormUiState by viewModel.formErrors.collectAsStateWithLifecycle()
    val isLoadingUser by viewModel.isLoadingUser.collectAsStateWithLifecycle()

    var isConfirmingSigningOut by remember { mutableStateOf(false) }
    var isEditingUserByDialog by remember { mutableStateOf(false) }

    var isSigningOut by remember { mutableStateOf(false) }
    var isEditingUser by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))

                    isEditingUser = false
                    isSigningOut = false
                }

                is EditUserSuccess -> {
                    showSnackbar(context.getString(uiEvent.messageId))

                    isEditingUser = false
                }

                is RedirectToSignIn -> {
                    onSignOut()

                    isSigningOut = false
                }
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        topBar = {
            ProfileScreenTopBar(onBack = onBack)
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
            if (isEditingUserByDialog) {
                EditUserDialog(
                    userUiState = userUiState,
                    editUserErrorUiState = editUserFormUiState,
                    onUserUiStateChange = viewModel::onUserChange,
                    onEdit = { editedUser ->
                        isEditingUser = true
                        isEditingUserByDialog = false

                        viewModel.editUser(editedUser)
                    },
                    onCancel = {
                        isEditingUserByDialog = false

                        viewModel.clearErrors()
                    },
                )
            }

            if (isConfirmingSigningOut) {
                ConfirmSignOutUserDialog(
                    onConfirmSignOut = {
                        isSigningOut = true
                        isConfirmingSigningOut = false

                        coroutineScope.launch {
                            credentialManager.clearGoogleCredentialState(
                                activityContext = activity,
                            )
                                .onError { e ->
                                    showSnackbar(context.getString(e.toUiMessage()))
                                    isSigningOut = false
                                }
                                .onSuccess {
                                    viewModel.signOutUser()
                                }
                        }
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
                    onSignOut = { isConfirmingSigningOut = true },
                    onEditUser = {
                        if (!isEditingUserByDialog) isEditingUserByDialog = true
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        ProfileScreen(
            viewModel = koinViewModel(),
            credentialManager = koinInject(),
            modifier = modifier
        )
    }
}
