package com.fstengineering.leasilycrm.core.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fstengineering.leasilycrm.R

val InstrumentSans = FontFamily(
    Font(R.font.instrument_sans)
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(
        fontFamily = InstrumentSans,
        fontSize = 57.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = baseline.displayMedium.copy(
        fontFamily = InstrumentSans,
        fontSize = 45.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = baseline.displaySmall.copy(
        fontFamily = InstrumentSans,
        fontSize = 36.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = baseline.headlineLarge.copy(
        fontFamily = InstrumentSans,
        fontSize = 32.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = baseline.headlineMedium.copy(
        fontFamily = InstrumentSans,
        fontSize = 28.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = baseline.headlineSmall.copy(
        fontFamily = InstrumentSans,
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = baseline.titleLarge.copy(
        fontFamily = InstrumentSans,
        fontSize = 22.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = baseline.titleMedium.copy(
        fontFamily = InstrumentSans,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleSmall = baseline.titleSmall.copy(
        fontFamily = InstrumentSans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = baseline.bodyLarge.copy(
        fontFamily = InstrumentSans,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = baseline.bodyMedium.copy(
        fontFamily = InstrumentSans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = baseline.bodySmall.copy(
        fontFamily = InstrumentSans,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = baseline.labelLarge.copy(
        fontFamily = InstrumentSans,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = baseline.labelMedium.copy(
        fontFamily = InstrumentSans,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = baseline.labelSmall.copy(
        fontFamily = InstrumentSans,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)
