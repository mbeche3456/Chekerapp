package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class PalindromeSemanticColors(
  val success: Color,
  val successContainer: Color,
  val onSuccessContainer: Color,
  val notMatch: Color,
  val notMatchContainer: Color,
  val onNotMatchContainer: Color,
  val warning: Color,
  val warningContainer: Color,
  val onWarningContainer: Color,
)

private val LightSemanticColors =
  PalindromeSemanticColors(
    success = GreenSuccess,
    successContainer = GreenSuccessContainer,
    onSuccessContainer = GreenOnSuccessContainer,
    notMatch = RedError,
    notMatchContainer = RedErrorContainer,
    onNotMatchContainer = RedOnErrorContainer,
    warning = AmberWarning,
    warningContainer = AmberWarningContainer,
    onWarningContainer = AmberOnWarningContainer,
  )

private val DarkSemanticColors =
  PalindromeSemanticColors(
    success = GreenSuccessDark,
    successContainer = GreenSuccessContainerDark,
    onSuccessContainer = GreenOnSuccessContainerDark,
    notMatch = RedErrorDark,
    notMatchContainer = RedErrorContainerDark,
    onNotMatchContainer = RedOnErrorContainerDark,
    warning = AmberWarningDark,
    warningContainer = AmberWarningContainerDark,
    onWarningContainer = AmberOnWarningContainerDark,
  )

val LocalPalindromeSemanticColors = staticCompositionLocalOf { LightSemanticColors }

private val DarkColorScheme =
  darkColorScheme(
    primary = SoftLavenderPrimary,
    onPrimary = SoftLavenderOnPrimary,
    primaryContainer = SoftLavenderContainerDark,
    onPrimaryContainer = SoftLavenderOnContainerDark,
    secondary = VioletSecondaryDark,
    onSecondary = AmethystSurface,
    secondaryContainer = VioletContainerDark,
    onSecondaryContainer = DarkInkPrimary,
    tertiary = LilacTertiaryDark,
    onTertiary = AmethystSurface,
    tertiaryContainer = LilacContainerDark,
    onTertiaryContainer = SoftLavenderOnContainerDark,
    background = AmethystBackground,
    onBackground = DarkInkPrimary,
    surface = AmethystSurface,
    onSurface = DarkInkPrimary,
    surfaceVariant = AmethystContainer,
    onSurfaceVariant = DarkInkSecondary,
    surfaceContainerLow = AmethystSurface,
    surfaceContainer = AmethystContainer,
    surfaceContainerHigh = AmethystHigh,
    outline = DarkLavenderOutline,
    outlineVariant = AmethystHigh,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = DeepPurplePrimary,
    onPrimary = WhiteSurface,
    primaryContainer = DeepPurpleContainer,
    onPrimaryContainer = DeepPurpleOnContainer,
    secondary = SoftVioletSecondary,
    onSecondary = WhiteSurface,
    secondaryContainer = SoftVioletContainer,
    onSecondaryContainer = DeepPurpleOnContainer,
    tertiary = AccentVioletTertiary,
    onTertiary = WhiteSurface,
    tertiaryContainer = AccentVioletContainer,
    onTertiaryContainer = DeepPurpleOnContainer,
    background = LightLavenderBackground,
    onBackground = InkPrimary,
    surface = WhiteSurface,
    onSurface = InkPrimary,
    surfaceVariant = LavenderSurfaceContainer,
    onSurfaceVariant = InkSecondary,
    surfaceContainerLow = LightLavenderBackground,
    surfaceContainer = LavenderSurfaceContainer,
    surfaceContainerHigh = LavenderSurfaceHigh,
    outline = LavenderOutline,
    outlineVariant = SoftLavenderOutlineVariant,
  )

val AppShapes =
  Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
  )

@Composable
fun PalindromeCheckerTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val semanticColors = if (darkTheme) DarkSemanticColors else LightSemanticColors

  CompositionLocalProvider(LocalPalindromeSemanticColors provides semanticColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      shapes = AppShapes,
      content = content,
    )
  }
}
