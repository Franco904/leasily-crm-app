package com.fstengineering.leasilycrm.signin.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator

@Composable
fun GoogleSignInButton(
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
                .size(width = 164.dp, height = 32.dp)
        ) {
            if (isSigningIn) {
                AppCircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(24.dp)
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
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
                    )
                }
            }
        }
    }
}
