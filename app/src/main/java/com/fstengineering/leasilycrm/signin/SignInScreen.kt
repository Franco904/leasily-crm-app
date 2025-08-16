package com.fstengineering.leasilycrm.signin

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.theme.AppTheme
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.external.interfaces.CredentialManagerAdapter
import com.fstengineering.leasilycrm.core.presentation.utils.CredentialsError
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.signin.SignInViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.signin.SignInViewModel.UiEvents.SignInSuccess
import com.fstengineering.leasilycrm.signin.composables.GoogleSignInButton
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    credentialManager: CredentialManagerAdapter,
    modifier: Modifier = Modifier,
    onSignInSuccess: () -> Unit = {},
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val coroutineScope = rememberCoroutineScope()

    val primaryColor = MaterialTheme.colorScheme.primary

    var isSigningIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    isSigningIn = false

                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is SignInSuccess -> {
                    isSigningIn = false

                    onSignInSuccess()
                }
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = primaryColor.toArgb()
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
                        coroutineScope.launch {
                            isSigningIn = true

                            credentialManager.chooseGoogleAccount(
                                activityContext = activity,
                                isSignIn = true,
                                mustEnableAutoSelect = !viewModel.didUserExplicitlySignOut,
                            )
                                .fold(
                                    onError = { e ->
                                        if (e == CredentialsError.UserCancellation) {
                                            return@fold AppResult.Error(error = e)
                                        }

                                        return@fold credentialManager.chooseGoogleAccount(
                                            activityContext = activity,
                                            isSignIn = false,
                                            mustEnableAutoSelect = false,
                                        )
                                    },
                                    onSuccess = { data -> return@fold AppResult.Success(data = data) }
                                )
                                .onError { e ->
                                    showSnackbar(context.getString(e.toUiMessage()))
                                    isSigningIn = false
                                }
                                .onSuccess { credentials ->
                                    viewModel.onSignInUser(credentials = credentials)
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

@Preview
@Composable
fun SignInScreenPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        SignInScreen(
            viewModel = koinViewModel(),
            credentialManager = koinInject(),
            modifier = modifier,
        )
    }
}
