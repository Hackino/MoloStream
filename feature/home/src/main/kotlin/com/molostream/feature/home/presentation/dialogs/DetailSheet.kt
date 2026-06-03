package com.molostream.feature.home.presentation.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.molostream.core.designsystem.component.AssetImage
import com.molostream.core.designsystem.component.MoloPrimaryButton
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.model.Movie
import com.molostream.core.designsystem.R as DsR

/**
 * Floating detail dialog opened by "More info" — a rounded card docked to the
 * bottom of the screen, inset from the edges, with a close (X) button. Tapping
 * the dimmed area above it dismisses. Mirrors the React detail card.
 */
@Composable
fun DetailSheet(
    movie: Movie,
    subscribed: Boolean,
    onPlay: (Movie) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MoloTheme.colors
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        // Fill the window; dock the card to the bottom. Taps outside the card dismiss.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onDismiss),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .navigationBarsPadding()
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surface1)
                    .border(1.dp, colors.hairline, RoundedCornerShape(24.dp))
                    // Absorb taps on the card so they don't dismiss the dialog.
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = {}),
            ) {
                DetailImageHeader(movie = movie, surfaceColor = colors.surface1, onDismiss = onDismiss)

                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .border(1.dp, colors.text.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(movie.rating, color = colors.text, style = MaterialTheme.typography.labelSmall)
                        }
                        if (movie.isLive) {
                            LiveBadge()
                        } else {
                            Text(
                                stringResource(DsR.string.movie_year_rating, movie.year, movie.genre),
                                color = colors.textDim,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                        PlanBadge(subscribed)
                    }

                    Text(movie.description, color = colors.textDim, style = MaterialTheme.typography.bodyLarge)

                    MoloPrimaryButton(
                        text = stringResource(DsR.string.action_play),
                        leadingIcon = Icons.Filled.PlayArrow,
                        onClick = { onPlay(movie) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailImageHeader(movie: Movie, surfaceColor: androidx.compose.ui.graphics.Color, onDismiss: () -> Unit) {
    val colors = MoloTheme.colors
    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        AssetImage(
            assetPath = movie.posterPath,
            contentDescription = movie.title,
            modifier = Modifier.fillMaxWidth().height(200.dp),
        )
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp).background(
                Brush.verticalGradient(0.35f to Color.Transparent, 1f to surfaceColor),
            ),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(DsR.string.cd_close),
                tint = colors.text,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            movie.title,
            color = colors.text,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
        )
    }
}

@Composable
private fun LiveBadge() {
    val colors = MoloTheme.colors
    Text(
        text = stringResource(DsR.string.media_live),
        color = colors.onAccent,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.live)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
private fun PlanBadge(subscribed: Boolean) {
    val colors = MoloTheme.colors
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (subscribed) colors.accentInk else colors.surface3)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        if (subscribed) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = colors.accentSoft, modifier = Modifier.size(12.dp))
        }
        Text(
            stringResource(if (subscribed) DsR.string.plan_ad_free else DsR.string.plan_with_ads),
            color = if (subscribed) colors.accentSoft else colors.textDim,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
