package com.fstengineering.leasilycrm.core.application.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun customColor(
    lightColor: Color,
    darkColor: Color,
) = if (isSystemInDarkTheme()) darkColor else lightColor

val cyan
    @Composable get() = customColor(
        lightColor = Color(0xFFB2EBF2),
        darkColor = Color(0xFF00838F),
    )

val deepOrange
    @Composable get() = customColor(
        lightColor = Color(0xFFFFCCBC),
        darkColor = Color(0xFFD84315),
    )

val purple
    @Composable get() = customColor(
        lightColor = Color(0xFFE1BEE7),
        darkColor = Color(0xFF6A1B9A),
    )

val lime
    @Composable get() = customColor(
        lightColor = Color(0xFFE6EE9C),
        darkColor = Color(0xFFAFB42B),
    )

val pink
    @Composable get() = customColor(
        lightColor = Color(0xFFF8BBD0),
        darkColor = Color(0xFFAD1457),
    )

val indigo
    @Composable get() = customColor(
        lightColor = Color(0xFFC5CAE9),
        darkColor = Color(0xFF283593),
    )

val teal
    @Composable get() = customColor(
        lightColor = Color(0xFFB2DFDB),
        darkColor = Color(0xFF00695C),
    )

val brown
    @Composable get() = customColor(
        lightColor = Color(0xFFD7CCC8),
        darkColor = Color(0xFF4E342E),
    )

val lightBlue
    @Composable get() = customColor(
        lightColor = Color(0xFFB3E5FC),
        darkColor = Color(0xFF0277BD),
    )

val orange
    @Composable get() = customColor(
        lightColor = Color(0xFFFFE0B2),
        darkColor = Color(0xFFEF6C00),
    )

val blueGrey
    @Composable get() = customColor(
        lightColor = Color(0xFFCFD8DC),
        darkColor = Color(0xFF37474F),
    )

val amber
    @Composable get() = customColor(
        lightColor = Color(0xFFFFECB3),
        darkColor = Color(0xFFFF8F00),
    )

val grey
    @Composable get() = customColor(
        lightColor = Color(0xFFBDBDBD),
        darkColor = Color(0xFF9E9E9E),
    )

val customColors
    @Composable get() = listOf(
        cyan,
        deepOrange,
        purple,
        lime,
        pink,
        indigo,
        teal,
        brown,
        lightBlue,
        orange,
        blueGrey,
        amber,
        grey,
    )

val customColorsV2
    @Composable get() = listOf(
        amber,
        teal,
        lightBlue,
        grey,
        pink,
        brown,
        indigo,
        orange,
        lime,
        cyan,
        deepOrange,
        purple,
        blueGrey,
    )

val customColorsV3
    @Composable get() = listOf(
        deepOrange,
        lime,
        cyan,
        brown,
        blueGrey,
        indigo,
        lightBlue,
        orange,
        teal,
        purple,
        amber,
        grey,
        pink,
    )

val customColorsV4
    @Composable get() = listOf(
        cyan,
        lime,
        pink,
        brown,
        deepOrange,
        teal,
        blueGrey,
        amber,
        purple,
        grey,
        indigo,
        lightBlue,
        orange,
    )
