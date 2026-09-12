package com.anyflow.journey.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Identitas visual Journey mengikuti mockup v0 user: primary wine #732E4B di
 * atas putih, teks #3A3A3A, kartu putih radius besar, heading serif
 * (mockup pakai Fraunces — di sini serif sistem, tanpa unduh font).
 */
object Brand {
    val Wine = Color(0xFF732E4B)
    val WineDeep = Color(0xFF522035)
    val WineSoft = Color(0xFFF7ECF1)
    val Rose = Color(0xFFB05A78)

    val Ink = Color(0xFF3A3A3A)
    val Body = Color(0xFF4A4A4A)
    val Muted = Color(0xFF8C8C8C)
    val Line = Color(0xFFEBE4E8)

    val Paper = Color(0xFFFFFFFF)
    val Canvas = Color(0xFFFFFFFF)
    val Cream = Color(0xFFFBF8F9)

    val Green = Color(0xFF2F7A5A)
    val GreenSoft = Color(0xFFE7F3ED)
    val Gold = Color(0xFFC08A2E)
    val GoldSoft = Color(0xFFFBF2E0)
    val BlueSoft = Color(0xFFEAF1FB)
    val Blue = Color(0xFF3A6EA5)
}

private val JourneyColors = lightColorScheme(
    primary = Brand.Wine,
    onPrimary = Color.White,
    primaryContainer = Brand.WineSoft,
    onPrimaryContainer = Brand.WineDeep,
    secondary = Brand.Rose,
    onSecondary = Color.White,
    secondaryContainer = Brand.WineSoft,
    onSecondaryContainer = Brand.WineDeep,
    tertiary = Brand.Gold,
    onTertiary = Color.White,
    background = Brand.Canvas,
    onBackground = Brand.Ink,
    surface = Brand.Paper,
    onSurface = Brand.Ink,
    surfaceVariant = Brand.Cream,
    onSurfaceVariant = Brand.Body,
    outline = Brand.Line,
    error = Color(0xFFB3261E),
    onError = Color.White,
)

private val JourneyTypography = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Brand.Ink),
    headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Brand.Ink),
    headlineSmall = TextStyle(fontFamily = FontFamily.Serif, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Brand.Ink),
    titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Brand.Ink),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Brand.Ink),
    titleSmall = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Brand.Ink),
    bodyLarge = TextStyle(fontSize = 15.sp, color = Brand.Body, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontSize = 13.5.sp, color = Brand.Body, lineHeight = 20.sp),
    bodySmall = TextStyle(fontSize = 12.sp, color = Brand.Muted, lineHeight = 17.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp),
)

private val JourneyShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun JourneyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JourneyColors,
        typography = JourneyTypography,
        shapes = JourneyShapes,
        content = content,
    )
}
