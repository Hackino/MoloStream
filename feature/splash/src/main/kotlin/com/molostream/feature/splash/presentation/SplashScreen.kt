package com.molostream.feature.splash.presentation

import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.feature.splash.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Total cold-start dwell before handing off to Home. */
private const val SPLASH_DURATION_MS = 3000L
private const val MARK_DRAW_MS = 1300
private const val WORDMARK_DELAY_MS = 760L

/** Warm top tint for the atmospheric background (matches the launcher tile). */
private val WarmTop = Color(0xFF1C140C)

/**
 * Animated brand splash: the mango "m" writes itself in, a glow breathes behind
 * it, and the wordmark rises into place — then [onFinished] is invoked after
 * [SPLASH_DURATION_MS]. Respects the system "remove animations" setting.
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MoloTheme.colors
    val animationsEnabled = rememberAnimationsEnabled()

    val draw = remember { Animatable(0f) }
    var showWordmark by remember { mutableStateOf(false) }

    LaunchedEffectOnce {
        if (animationsEnabled) {
            launch { draw.animateTo(1f, tween(MARK_DRAW_MS, easing = FastOutSlowInEasing)) }
            delay(WORDMARK_DELAY_MS)
            showWordmark = true
            delay(SPLASH_DURATION_MS - WORDMARK_DELAY_MS)
        } else {
            draw.snapTo(1f)
            showWordmark = true
            delay(900)
        }
        onFinished()
    }

    val glowTransition = rememberInfiniteTransition(label = "glow")
    val glow by glowTransition.animateFloatValue()

    SplashContent(
        modifier = modifier,
        drawProgress = draw.value,
        glow = glow,
        showWordmark = showWordmark,
    )
}

@Composable
private fun SplashContent(
    modifier: Modifier,
    drawProgress: Float,
    glow: Float,
    showWordmark: Boolean,
) {
    val colors = MoloTheme.colors
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(WarmTop, colors.background, colors.backgroundDeep)),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(360.dp)
                .alpha(drawProgress)
                .background(
                    Brush.radialGradient(
                        listOf(colors.accent.copy(alpha = 0.18f * glow), Color.Transparent),
                    ),
                ),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AnimatedMoloMark(
                progress = drawProgress,
                glow = glow,
                accentSoft = colors.accentSoft,
                accent = colors.accent,
                accentHot = colors.accentHot,
                modifier = Modifier.size(176.dp),
            )
            AnimatedVisibility(
                visible = showWordmark,
                enter = fadeIn(tween(560)) +
                    slideInVertically(tween(560, easing = FastOutSlowInEasing)) { it / 3 },
            ) {
                SplashWordmark()
            }
        }
    }
}

@Composable
private fun SplashWordmark() {
    val colors = MoloTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.splash_wordmark),
            style = MaterialTheme.typography.headlineLarge,
            color = colors.text,
        )
        Text(
            text = stringResource(R.string.splash_tagline),
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 3.sp),
            color = colors.textMute,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

/** Subtle "breathing" glow multiplier (0.62 ↔ 1.0). */
@Composable
private fun androidx.compose.animation.core.InfiniteTransition.animateFloatValue() =
    animateFloat(
        initialValue = 0.62f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowPulse",
    )

/** True unless the user has disabled animations system-wide. */
@Composable
private fun rememberAnimationsEnabled(): Boolean {
    val context = LocalContext.current
    return remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) != 0f
    }
}

/** A keyed [androidx.compose.runtime.LaunchedEffect] that runs exactly once. */
@Composable
private fun LaunchedEffectOnce(block: suspend kotlinx.coroutines.CoroutineScope.() -> Unit) {
    androidx.compose.runtime.LaunchedEffect(Unit, block)
}
