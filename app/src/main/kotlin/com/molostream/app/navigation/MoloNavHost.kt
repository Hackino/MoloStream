package com.molostream.app.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.molostream.core.designsystem.component.MoloPrimaryButton
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.core.domain.usecase.ObserveSubscriptionUseCase
import com.molostream.feature.home.presentation.HomeScreen
import com.molostream.feature.player.presentation.PlayerScreen
import com.molostream.feature.splash.presentation.SplashScreen
import org.koin.compose.koinInject
import com.molostream.core.designsystem.R as DsR

/**
 * App navigation graph (Navigation 3): Home + a Player destination keyed by
 * [PlayerKey]. The player slides up on open / down on close. On Home (the root),
 * the system back press asks to close the app.
 */
@Composable
fun MoloNavHost() {
    val backStack = rememberNavBackStack(SplashKey)
    val activity = LocalActivity.current
    var showExitApp by remember { mutableStateOf(false) }

    // Live subscription state — passed into the player to decide ad insertion.
    val observeSubscription = koinInject<ObserveSubscriptionUseCase>()
    val subscribed by observeSubscription().collectAsStateWithLifecycle(initialValue = false)

    // At the root, back asks to leave the app (player handles its own back).
    // During the splash there's nothing to confirm — back just closes the app.
    BackHandler(enabled = backStack.size <= 1 && !showExitApp) {
        if (backStack.lastOrNull() == HomeKey) showExitApp = true else activity?.finish()
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        transitionSpec = { slideInVertically(initialOffsetY = { it }) togetherWith fadeOut() },
        popTransitionSpec = { fadeIn() togetherWith slideOutVertically(targetOffsetY = { it }) },
        predictivePopTransitionSpec = { fadeIn() togetherWith slideOutVertically(targetOffsetY = { it }) },
        entryProvider = entryProvider {
            entry<SplashKey> {
                // Animated brand splash; on completion, swap it for Home so Home
                // becomes the new root (back from Home then prompts to exit).
                SplashScreen(
                    onFinished = {
                        backStack.add(HomeKey)
                        backStack.remove(SplashKey)
                    },
                )
            }
            entry<HomeKey> {
                HomeScreen(onOpenMovie = { movie -> backStack.add(movie.toPlayerKey()) })
            }
            entry<PlayerKey> { key ->
                PlayerScreen(
                    args = key.toArgs(),
                    subscribed = subscribed,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        },
    )

    if (showExitApp) {
        ExitAppDialog(
            onConfirm = { showExitApp = false; activity?.finish() },
            onDismiss = { showExitApp = false },
        )
    }
}

@Composable
private fun ExitAppDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val colors = MoloTheme.colors
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(colors.surface1)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                stringResource(DsR.string.exit_app_title),
                color = colors.text,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                stringResource(DsR.string.exit_app_body),
                color = colors.textDim,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            MoloPrimaryButton(
                text = stringResource(DsR.string.exit_app_confirm),
                leadingIcon = Icons.AutoMirrored.Filled.ExitToApp,
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                stringResource(DsR.string.exit_app_dismiss),
                color = colors.textDim,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }
    }
}
