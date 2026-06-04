package com.molostream.feature.player.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import com.molostream.core.designsystem.theme.MoloTheme
import com.molostream.feature.player.domain.PlayerArgs
import com.molostream.feature.player.presentation.components.AdOverlay
import com.molostream.feature.player.presentation.components.PlayerActions
import com.molostream.feature.player.presentation.components.PlayerControls
import com.molostream.feature.player.presentation.dialogs.ExitConfirmDialog
import com.molostream.feature.player.presentation.dialogs.ResumePrompt
import com.molostream.feature.player.presentation.dialogs.SeekUpsellDialog
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/** Groups the three event callbacks passed into [PlayerOverlay]. */
private data class PlayerOverlayCallbacks(
    val requestExit: () -> Unit,
    val onToggleControls: () -> Unit,
    val onSeekBlocked: () -> Unit,
)

/**
 * Player route. Renders the ExoPlayer surface (also the IMA ad container) with
 * fully custom Compose controls. Opening a previously-watched title shows a
 * Resume / Start-over prompt; leaving (UI back or system back) confirms first.
 */
@Composable
fun PlayerScreen(
    args: PlayerArgs,
    subscribed: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: PlayerViewModel = koinViewModel(key = args.movieId) { parametersOf(args, subscribed) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var controlsVisible by remember { mutableStateOf(true) }
    var showExitConfirm by remember { mutableStateOf(false) }
    var showSeekUpsell by remember { mutableStateOf(false) }

    val requestExit = { showExitConfirm = true }

    DisposableEffect(Unit) { onDispose { viewModel.release() } }
    PlayerLifecycleObserver(viewModel)
    BackHandler(enabled = !showExitConfirm) { showExitConfirm = true }

    val promptVisible = state.resumePromptMs != null
    val shouldAutoHide = controlsVisible && state.isPlaying && !state.isPlayingAd && !promptVisible
    LaunchedEffect(shouldAutoHide) {
        if (shouldAutoHide) { delay(3200); controlsVisible = false }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        PlayerSurface(viewModel = viewModel)
        PlayerOverlay(
            args = args,
            state = state,
            subscribed = subscribed,
            controlsVisible = controlsVisible,
            viewModel = viewModel,
            callbacks = PlayerOverlayCallbacks(
                requestExit = requestExit,
                onToggleControls = { controlsVisible = !controlsVisible },
                onSeekBlocked = { showSeekUpsell = true },
            ),
        )
        PlayerSpinner(state = state, controlsVisible = controlsVisible, promptVisible = promptVisible)
    }

    state.resumePromptMs?.let { resumeMs ->
        ResumePrompt(
            resumeMs = resumeMs,
            onResume = viewModel::resumeFromSaved,
            onRestart = viewModel::startFromBeginning,
            onBack = requestExit,
        )
    }

    if (showExitConfirm) {
        ExitConfirmDialog(
            subscribed = subscribed,
            isLive = state.isLive,
            onConfirm = onBack,
            onDismiss = { showExitConfirm = false },
        )
    }

    if (showSeekUpsell) {
        SeekUpsellDialog(
            onGoToPremium = { showSeekUpsell = false; requestExit() },
            onDismiss = { showSeekUpsell = false },
        )
    }
}

/** Pauses playback when the app goes to background; resumes when it returns. */
@Composable
private fun PlayerLifecycleObserver(viewModel: PlayerViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.pauseForBackground()
                Lifecycle.Event.ON_RESUME -> viewModel.resumeFromBackground()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
private fun PlayerSurface(viewModel: PlayerViewModel) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PlayerView(context).apply {
                useController = false
                setBackgroundColor(android.graphics.Color.BLACK)
                viewModel.initialize(this)
                player = viewModel.player
            }
        },
        update = { it.player = viewModel.player },
    )
}

@Composable
private fun PlayerOverlay(
    args: PlayerArgs,
    state: PlayerUiState,
    subscribed: Boolean,
    controlsVisible: Boolean,
    viewModel: PlayerViewModel,
    callbacks: PlayerOverlayCallbacks,
) {
    val promptVisible = state.resumePromptMs != null
    if (!state.isPlayingAd && !promptVisible && !state.preparing) {
        Box(
            modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures(onTap = { callbacks.onToggleControls() })
            },
        )
    }

    AnimatedVisibility(
        visible = controlsVisible && !state.isPlayingAd && !promptVisible && !state.preparing,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        PlayerControls(
            title = args.title,
            subtitle = args.subtitle,
            state = state,
            subscribed = subscribed,
            actions = PlayerActions(
                onBack = callbacks.requestExit,
                onTogglePlay = viewModel::togglePlayPause,
                onSeekBy = viewModel::seekBy,
                onSeekTo = viewModel::seekTo,
                onSeekBlocked = callbacks.onSeekBlocked,
            ),
        )
    }

    if (state.isPlayingAd) {
        AdOverlay(state = state, onBack = callbacks.requestExit)
    }
}

@Composable
private fun PlayerSpinner(state: PlayerUiState, controlsVisible: Boolean, promptVisible: Boolean) {
    val showSpinner = state.preparing ||
        (state.isBuffering && !controlsVisible && !state.isPlayingAd && !promptVisible)
    if (showSpinner) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MoloTheme.colors.accentHot,
                trackColor = Color.White.copy(alpha = 0.15f),
                strokeWidth = 3.dp,
            )
        }
    }
}
