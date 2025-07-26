package com.fstengineering.leasilycrm.core.application.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.fstengineering.leasilycrm.R

val InstrumentSans = FontFamily(
    Font(R.font.instrument_sans)
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = InstrumentSans),
    displayMedium = baseline.displayMedium.copy(fontFamily = InstrumentSans),
    displaySmall = baseline.displaySmall.copy(fontFamily = InstrumentSans),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = InstrumentSans),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = InstrumentSans),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = InstrumentSans),
    titleLarge = baseline.titleLarge.copy(fontFamily = InstrumentSans),
    titleMedium = baseline.titleMedium.copy(fontFamily = InstrumentSans),
    titleSmall = baseline.titleSmall.copy(fontFamily = InstrumentSans),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = InstrumentSans),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = InstrumentSans),
    bodySmall = baseline.bodySmall.copy(fontFamily = InstrumentSans),
    labelLarge = baseline.labelLarge.copy(fontFamily = InstrumentSans),
    labelMedium = baseline.labelMedium.copy(fontFamily = InstrumentSans),
    labelSmall = baseline.labelSmall.copy(fontFamily = InstrumentSans),
)
