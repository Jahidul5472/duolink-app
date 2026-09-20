package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = TerracottaSecondary,
    onPrimaryContainer = Color.White,
    secondary = BlushAccent,
    onSecondary = SlateDeep,
    secondaryContainer = SlateCard,
    onSecondaryContainer = SlateTextPrimary,
    tertiary = RoseGold,
    onTertiary = SlateDeep,
    background = SlateDeep,
    onBackground = SlateTextPrimary,
    surface = SlateSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder,
    outlineVariant = SlateCard
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = BlushSoft,
    onPrimaryContainer = TerracottaPrimary,
    secondary = TerracottaSecondary,
    onSecondary = Color.White,
    secondaryContainer = CreamCard,
    onSecondaryContainer = CreamTextPrimary,
    tertiary = RoseGold,
    onTertiary = CreamTextPrimary,
    background = CreamBackground,
    onBackground = CreamTextPrimary,
    surface = CreamSurface,
    onSurface = CreamTextPrimary,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = CreamTextSecondary,
    outline = CreamBorder,
    outlineVariant = CreamCard
  )

@Composable
fun TwofoldTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

// For backwards compatibility alias
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  TwofoldTheme(darkTheme = darkTheme, content = content)
}

