package com.molostream.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * MoloStream palette — the phase-1 "dark cinematic + mango" tokens converted
 * from OKLCH to sRGB. Held in a custom [MoloColors] holder so screens can read
 * brand colors (accent, ad, surfaces) that Material's ColorScheme doesn't model.
 */
@Immutable
data class MoloColors(
    val background: Color,
    val backgroundDeep: Color,
    val surface1: Color,
    val surface2: Color,
    val surface3: Color,
    val hairline: Color,
    val accent: Color,
    val accentSoft: Color,
    val accentHot: Color,
    val accentInk: Color,
    val onAccent: Color,
    val text: Color,
    val textDim: Color,
    val textMute: Color,
    val ad: Color,
    val live: Color,
    val success: Color,
)

val DarkMoloColors = MoloColors(
    background = Color(0xFF12100B),
    backgroundDeep = Color(0xFF0B0906),
    surface1 = Color(0xFF1A1712),
    surface2 = Color(0xFF222019),
    surface3 = Color(0xFF2C281F),
    hairline = Color(0x16FFFFFF),
    accent = Color(0xFFFFA12E),
    accentSoft = Color(0xFFFFC979),
    accentHot = Color(0xFFF77F2A),
    accentInk = Color(0xFF2A1A05),
    onAccent = Color(0xFF1A1206),
    text = Color(0xFFF4F1EA),
    textDim = Color(0xFFC8C2B6),
    textMute = Color(0xFF938C7F),
    ad = Color(0xFFFFC53D),
    live = Color(0xFFFF5470),
    success = Color(0xFF54D89A),
)

val LocalMoloColors = staticCompositionLocalOf { DarkMoloColors }
