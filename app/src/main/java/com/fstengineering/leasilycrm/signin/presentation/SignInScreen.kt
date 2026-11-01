package com.fstengineering.leasilycrm.signin.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.CredentialManagerAdapter
import com.fstengineering.leasilycrm.core.infra.utils.CredentialsError
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.SystemBarsEffect
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.signin.presentation.models.strategies.SignInErrorBottomSheetDataPropertiesStrategy
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

    var isSigningIn by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is SignInViewModel.UiEvent.BottomSheetError -> {
                    isSigningIn = false

                    val errorData = uiEvent.error.toBottomSheetData(
                        SignInErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is SignInViewModel.UiEvent.SignInSuccess -> {
                    isSigningIn = false

                    onSignIn()
                }
            }
        }
    }

    SystemBarsEffect(
        statusBarColor = MaterialTheme.colorScheme.primary,
        navigationBarColor = MaterialTheme.colorScheme.surface,
    )

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

                        coroutineScope.launch {
                            credentialManager.chooseGoogleAccount(
                                activityContext = activity,
                                mustEnableAutoSelect = !hasSessionExpired,
                            )
                                .onError { e ->
                                    isSigningIn = false

                                    if (e == CredentialsError.UserCancellation) return@onError

                                    onUiEvent(UiEvent.ShowErrorBottomSheet(e.toBottomSheetData()))
                                }
                                .onSuccess { idToken ->
                                    viewModel.signInUser(idToken)
                                }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun SignInSection(
    isSigningIn: Boolean,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(
            topStart = MaterialTheme.shapes.large.topStart,
            topEnd = MaterialTheme.shapes.large.topEnd,
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        ),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Box {
                Image(
                    painter = painterResource(R.drawable.logo_shadow),
                    contentDescription = null,
                    modifier = Modifier
                        .size(112.dp)
                        .offset(x = (-8).dp, y = 20.dp)
                )
                Image(
                    painter = painterResource(R.drawable.ic_leasily_crm_logo_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(112.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(R.string.sign_in_welcome_text),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                stringResource(R.string.sign_in_welcome_description),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            GoogleSignInButton(
                isSigningIn = isSigningIn,
                onSignIn = onSignIn,
            )
        }
    }
}

@Composable
private fun GoogleSignInButton(
    isSigningIn: Boolean,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        ),
        shape = CircleShape,
        onClick = onSignIn,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(
                    horizontal = 24.dp,
                    vertical = 8.dp,
                )
        ) {
            // Sempre composto (invisível ao carregar) para o botão não mudar de tamanho.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.alpha(if (isSigningIn) 0f else 1f)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_google_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.sign_in_sign_in_with_google),
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
            if (isSigningIn) {
                AppCircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }
    }
}
