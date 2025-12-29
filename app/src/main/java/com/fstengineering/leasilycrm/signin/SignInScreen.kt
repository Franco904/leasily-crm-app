package com.fstengineering.leasilycrm.signin

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.external.interfaces.CredentialManagerAdapter
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.utils.CredentialsError
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.signin.SignInViewModel.UiEvents.ContinueSignIn
import com.fstengineering.leasilycrm.signin.SignInViewModel.UiEvents.Error
import com.fstengineering.leasilycrm.signin.SignInViewModel.UiEvents.SignInSuccess
import com.fstengineering.leasilycrm.signin.composables.SignInSection
import com.fstengineering.leasilycrm.signin.models.strategies.SignInErrorBottomSheetDataPropertiesStrategy
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    hasSessionExpired: Boolean,
    viewModel: SignInViewModel,
    credentialManager: CredentialManagerAdapter,
    modifier: Modifier = Modifier,
    onSignIn: () -> Unit = {},
    onUiEvent: (UiEvent) -> Unit,
) {
    val activity = LocalActivity.current!!

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    var isSigningIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is Error -> {
                    isSigningIn = false

                    val errorData = uiEvent.error.toBottomSheetData(
                        SignInErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is ContinueSignIn -> {
                    launch {
                        credentialManager.chooseGoogleAccount(
                            activityContext = activity,
                            mustEnableAutoSelect = !hasSessionExpired,
                        )
                            .onError { e ->
                                isSigningIn = false

                                if (e == CredentialsError.UserCancellation) return@onError

                                onUiEvent(UiEvent.ShowErrorBottomSheet(e.toBottomSheetData()))
                            }
                            .onSuccess { credentials ->
                                viewModel.signInUser(credentials = credentials)
                            }
                    }
                }

                is SignInSuccess -> {
                    isSigningIn = false

                    onSignIn()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    SideEffect {
        activity.window.statusBarColor = primaryColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(layoutDirection = LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(layoutDirection = LayoutDirection.Ltr),
                )
        ) {
            Column(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .weight(0.6f)
                        .fillMaxWidth()
                        .background(primaryColor)
                )
                Box(
                    Modifier
                        .weight(0.4f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
            SignInSection(
                isSigningIn = isSigningIn,
                onSignIn = {
                    if (!isSigningIn) {
                        isSigningIn = true
                        viewModel.onSignIn()
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            )
        }
    }
}
