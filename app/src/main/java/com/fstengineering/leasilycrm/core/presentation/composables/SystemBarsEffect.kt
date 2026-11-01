package com.fstengineering.leasilycrm.core.presentation.composables

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Informa às system bars a cor que a tela desenha atrás delas, para escolher ícones com contraste.
 * Em API < 35 também pinta as barras; a partir da 35 (edge-to-edge forçado) quem pinta é o
 * conteúdo da tela.
 */
@Composable
fun SystemBarsEffect(
    statusBarColor: Color,
    navigationBarColor: Color,
) {
    val activity = LocalActivity.current ?: return
    val view = LocalView.current

    SideEffect {
        val window = activity.window

        @Suppress("DEPRECATION")
        window.statusBarColor = statusBarColor.toArgb()
        @Suppress("DEPRECATION")
        window.navigationBarColor = navigationBarColor.toArgb()

        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = statusBarColor.luminance() > LIGHT_LUMINANCE_THRESHOLD
            isAppearanceLightNavigationBars =
                navigationBarColor.luminance() > LIGHT_LUMINANCE_THRESHOLD
        }
    }
}

private const val LIGHT_LUMINANCE_THRESHOLD = 0.5f
