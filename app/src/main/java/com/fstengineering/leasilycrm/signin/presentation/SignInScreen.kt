package com.fstengineering.leasilycrm.signin.presentation

import androidx.activity.compose.LocalActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.CredentialManagerAdapter
import com.fstengineering.leasilycrm.core.infra.utils.CredentialsError
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.SystemBarsEffect
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.theme.AppTheme
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

    SignInContent(
        isSigningIn = isSigningIn,
        hasSessionExpired = hasSessionExpired,
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
        modifier = modifier,
    )
}


@Composable
private fun SignInContent(
    isSigningIn: Boolean,
    hasSessionExpired: Boolean,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SystemBarsEffect(
        statusBarColor = MaterialTheme.colorScheme.inversePrimary,
        navigationBarColor = MaterialTheme.colorScheme.primaryContainer,
    )

    BoxWithConstraints(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.inversePrimary,
                        MaterialTheme.colorScheme.primaryContainer,
                    )
                )
            )
    ) {
        val screenHeight = maxHeight

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .heightIn(min = screenHeight)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            if (hasSessionExpired) {
                SessionExpiredAlert(modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
            }
            SignInCard(
                isSigningIn = isSigningIn,
                onSignIn = onSignIn,
            )
        }
    }
}

@Composable
private fun SignInCard(
    isSigningIn: Boolean,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_leasily_crm_logo_foreground),
                contentDescription = null,
                modifier = Modifier.size(88.dp)
            )
            Text(
                text = stringResource(R.string.sign_in_welcome_text),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.semantics { heading() }
            )
            Spacer(Modifier.height(20.dp))
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SIGN_IN_BENEFITS.forEach { benefit ->
                    BenefitRow(
                        icon = benefit.icon,
                        text = stringResource(benefit.text),
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            GoogleSignInButton(
                isSigningIn = isSigningIn,
                onSignIn = onSignIn,
            )
        }
    }
}

private data class SignInBenefit(
    val icon: ImageVector,
    @StringRes val text: Int,
)

private val SIGN_IN_BENEFITS = listOf(
    SignInBenefit(Icons.Outlined.Home, R.string.sign_in_benefit_imoveis_proprietarios),
    SignInBenefit(Icons.Outlined.DateRange, R.string.sign_in_benefit_periodos_ocupados),
    SignInBenefit(Icons.Outlined.Person, R.string.sign_in_benefit_leads_inquilinos_atendimentos),
    SignInBenefit(Icons.Outlined.Handshake, R.string.sign_in_benefit_opportunities),
    SignInBenefit(Icons.Outlined.WifiOff, R.string.sign_in_benefit_offline),
)

@Composable
private fun BenefitRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun SessionExpiredAlert(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.sign_in_session_expired),
                style = MaterialTheme.typography.bodyLarge,
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
    val signingInDescription = stringResource(R.string.sign_in_signing_in)

    Surface(
        onClick = onSignIn,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .then(
                if (isSigningIn) {
                    // O rótulo do botão continua na árvore (alpha 0 não o remove): substitui tudo.
                    Modifier.clearAndSetSemantics {
                        role = Role.Button
                        contentDescription = signingInDescription
                        disabled()
                    }
                } else {
                    Modifier.semantics { role = Role.Button }
                }
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Sempre composto (invisível ao carregar) para o botão não mudar de tamanho.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.alpha(if (isSigningIn) 0f else 1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(32.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_google_logo),
                        contentDescription = null,
                        modifier = Modifier.padding(2.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
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
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SignInContentPreview() {
    AppTheme {
        SignInContent(
            isSigningIn = false,
            hasSessionExpired = false,
            onSignIn = {},
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SignInContentSessionExpiredSigningInPreview() {
    AppTheme {
        SignInContent(
            isSigningIn = true,
            hasSessionExpired = true,
            onSignIn = {},
        )
    }
}

@Preview(showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SignInContentDarkPreview() {
    AppTheme {
        SignInContent(
            isSigningIn = false,
            hasSessionExpired = true,
            onSignIn = {},
        )
    }
}

@Preview(showSystemUi = true, fontScale = 2f, widthDp = 640, heightDp = 360)
@Composable
private fun SignInContentLandscapeLargeFontPreview() {
    AppTheme {
        SignInContent(
            isSigningIn = false,
            hasSessionExpired = true,
            onSignIn = {},
        )
    }
}
