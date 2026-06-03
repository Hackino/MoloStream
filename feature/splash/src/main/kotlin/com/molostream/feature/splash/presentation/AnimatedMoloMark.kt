package com.molostream.feature.splash.presentation

import android.graphics.PathMeasure
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform

/**
 * The MoloStream "m" mark, drawn on a 1024×1024 design grid (identical geometry
 * to the launcher icon) with a staggered "draw-on" stroke reveal and a soft
 * layered mango glow.
 *
 * @param progress 0f → 1f reveal progress for the whole mark.
 * @param glow     0f → 1f multiplier pulsing the glow intensity.
 */
@Composable
fun AnimatedMoloMark(
    progress: Float,
    glow: Float,
    modifier: Modifier = Modifier,
    accentSoft: Color,
    accent: Color,
    accentHot: Color,
) {
    // One continuous contour per stroke, in 1024-space (matches splash_logo.xml).
    val contours = remember {
        listOf(
            Path().apply { moveTo(324f, 690f); lineTo(324f, 396f) },
            Path().apply {
                moveTo(324f, 470f)
                cubicTo(324f, 408f, 372f, 372f, 432f, 372f)
                cubicTo(492f, 372f, 512f, 420f, 512f, 478f)
                lineTo(512f, 690f)
            },
            Path().apply {
                moveTo(512f, 470f)
                cubicTo(512f, 408f, 560f, 372f, 620f, 372f)
                cubicTo(680f, 372f, 700f, 420f, 700f, 478f)
                lineTo(700f, 690f)
            },
        )
    }
    val measures = remember(contours) {
        contours.map { PathMeasure(it.asAndroidPath(), false) }
    }
    // Per-stroke timing windows so the mark "writes" itself: stem, then arches.
    val windows = remember { listOf(0.00f to 0.45f, 0.20f to 0.78f, 0.42f to 1.00f) }

    Canvas(modifier) {
        val s = size.minDimension / GRID
        val markScale = 0.86f + 0.14f * progress.coerceIn(0f, 1f)
        val brush = Brush.linearGradient(
            colors = listOf(accentSoft, accent, accentHot),
            start = Offset(300f, 360f),
            end = Offset(724f, 680f),
        )
        withTransform({
            scale(s, s, pivot = Offset.Zero)
            // optical-centre the mark (its bbox centre sits a touch below grid centre)
            translate(top = -16f)
            scale(markScale, markScale, pivot = Offset(GRID / 2f, GRID / 2f))
        }) {
            measures.forEachIndexed { i, measure ->
                val (from, to) = windows[i]
                val local = ((progress - from) / (to - from)).coerceIn(0f, 1f)
                if (local <= 0f) return@forEachIndexed
                val eased = FastOutSlowInEasing.transform(local)
                drawContour(measure, eased, brush, glow)
            }
        }
    }
}

/** Draws a [fraction] of one contour with a triple-layer mango glow + gradient. */
private fun DrawScope.drawContour(
    measure: PathMeasure,
    fraction: Float,
    brush: Brush,
    glow: Float,
) {
    val segment = android.graphics.Path()
    measure.getSegment(0f, measure.length * fraction, segment, true)
    val path = segment.asComposePath()

    // soft outer halo
    drawPath(path, GlowColor.copy(alpha = 0.16f * glow), style = stroke(STROKE * 2.6f))
    // tighter inner glow
    drawPath(path, GlowColor.copy(alpha = 0.22f * glow), style = stroke(STROKE * 1.7f))
    // crisp gradient stroke
    drawPath(path, brush = brush, style = stroke(STROKE))
}

private fun stroke(width: Float) =
    Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)

private const val GRID = 1024f
private const val STROKE = 84f
private val GlowColor = Color(0xFFFFA12E)
