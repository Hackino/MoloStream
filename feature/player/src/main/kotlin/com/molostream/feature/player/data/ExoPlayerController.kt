package com.molostream.feature.player.data

import android.content.Context
import android.net.Uri
import androidx.media3.common.AdViewProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ima.ImaAdsLoader
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.molostream.core.domain.usecase.GetResumePositionUseCase
import com.molostream.core.domain.usecase.SaveWatchProgressUseCase
import com.molostream.core.model.WatchProgress
import com.molostream.feature.player.domain.PlayerArgs
import com.molostream.feature.player.domain.PlayerController
import com.molostream.feature.player.presentation.PlayerUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * ExoPlayer + Google IMA implementation of [PlayerController].
 *
 * Free users (subscribed = false) get the VMAP ad tag attached to the media
 * item, so IMA inserts pre/mid/post-roll and ExoPlayer resumes the content
 * seamlessly after each break. Subscribers get the HLS content only. Position
 * is restored on start and saved continuously for resume.
 */
class ExoPlayerController(
    private val appContext: Context,
    private val args: PlayerArgs,
    private val subscribed: Boolean,
    private val getResumePosition: GetResumePositionUseCase,
    private val saveWatchProgress: SaveWatchProgressUseCase,
) : PlayerController {

    private val _uiState = MutableStateFlow(PlayerUiState(adsEnabled = !subscribed && !args.isLive, isLive = args.isLive))
    override val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    override var player: ExoPlayer? = null
        private set

    private var adsLoader: ImaAdsLoader? = null
    // Recreated in initialize() if a prior release() cancelled it.
    private var scope = newScope()
    private val saveScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var lastSavedAt = 0L
    private var progressJob: Job? = null

    private val listener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) = syncState(player)
    }

    override fun initialize(adViewProvider: AdViewProvider) {
        if (player != null) return

        // If this controller was previously released (scope cancelled) and is being
        // reused for the same movie, a fresh scope is needed for coroutines to work.
        if (!scope.isActive) scope = newScope()

        val mediaSourceFactory = DefaultMediaSourceFactory(appContext)
        val mediaItemBuilder = MediaItem.Builder().setUri(args.hlsUrl)

        if (!subscribed && !args.isLive) {
            val loader = ImaAdsLoader.Builder(appContext).build()
            adsLoader = loader
            mediaSourceFactory.setLocalAdInsertionComponents({ loader }, adViewProvider)
            mediaItemBuilder.setAdsConfiguration(
                MediaItem.AdsConfiguration.Builder(Uri.parse(args.adTagUrl)).build(),
            )
        }

        val exo = ExoPlayer.Builder(appContext).setMediaSourceFactory(mediaSourceFactory).build()
        adsLoader?.setPlayer(exo)
        exo.addListener(listener)
        exo.setMediaItem(mediaItemBuilder.build())
        exo.prepare()
        player = exo
        startProgressLoop()

        // Live: no resume — ExoPlayer auto-starts at the live edge.
        if (args.isLive) {
            exo.playWhenReady = true
            _uiState.value = _uiState.value.copy(preparing = false)
            return
        }

        // VOD: decide before showing any controls — resume → prompt; otherwise auto-play.
        // Fetched inline so this coroutine is always on the current (possibly fresh) scope.
        scope.launch {
            val resumeMs = getResumePosition(args.movieId)
            if (resumeMs > 0) {
                exo.seekTo(resumeMs) // scrubber shows the saved spot immediately
                _uiState.value = _uiState.value.copy(preparing = false, resumePromptMs = resumeMs)
            } else {
                exo.playWhenReady = true
                _uiState.value = _uiState.value.copy(preparing = false)
            }
        }
    }

    override fun resumeFromSaved() {
        _uiState.value = _uiState.value.copy(resumePromptMs = null)
        player?.playWhenReady = true
    }

    override fun startFromBeginning() {
        _uiState.value = _uiState.value.copy(resumePromptMs = null)
        player?.apply { seekTo(0); playWhenReady = true }
    }

    override fun togglePlayPause() {
        val p = player ?: return
        if (p.isPlaying) p.pause() else p.play()
    }

    override fun seekBy(deltaMs: Long) {
        val p = player ?: return
        p.seekTo((p.contentPosition + deltaMs).coerceIn(0L, p.contentDuration.coerceAtLeast(0L)))
    }

    override fun seekTo(positionMs: Long) {
        player?.seekTo(positionMs)
    }

    private fun startProgressLoop() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                player?.let { syncState(it); maybePersist(it) }
                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private fun syncState(p: Player) {
        val contentDuration = p.contentDuration.takeIf { it > 0 } ?: 0L
        _uiState.value = _uiState.value.copy(
            isPlaying = p.isPlaying,
            isBuffering = p.playbackState == Player.STATE_BUFFERING,
            ended = p.playbackState == Player.STATE_ENDED,
            contentPositionMs = p.contentPosition.coerceAtLeast(0L),
            contentDurationMs = contentDuration,
            bufferedPositionMs = p.contentBufferedPosition.coerceAtLeast(0L),
            isPlayingAd = p.isPlayingAd,
            adPositionMs = if (p.isPlayingAd) p.currentPosition.coerceAtLeast(0L) else 0L,
            adDurationMs = if (p.isPlayingAd) p.duration.takeIf { it > 0 } ?: 0L else 0L,
        )
    }

    private fun maybePersist(p: Player) {
        if (p.isPlayingAd) return
        val now = System.currentTimeMillis()
        if (now - lastSavedAt < SAVE_INTERVAL_MS) return
        lastSavedAt = now
        persist(p.contentPosition, p.contentDuration)
    }

    private fun persist(positionMs: Long, durationMs: Long) {
        if (args.isLive) return // live streams aren't saved or resumed
        if (durationMs <= 0) return
        val progress = WatchProgress(
            movieId = args.movieId,
            positionMs = positionMs.coerceAtLeast(0L),
            durationMs = durationMs,
            updatedAt = System.currentTimeMillis(),
        )
        saveScope.launch { saveWatchProgress(progress) }
    }

    override fun release() {
        val p = player ?: return
        persist(p.contentPosition, p.contentDuration)
        p.removeListener(listener)
        adsLoader?.setPlayer(null)
        p.release()
        adsLoader?.release()
        player = null
        adsLoader = null
        progressJob?.cancel()
        progressJob = null
        scope.cancel()
    }

    private companion object {
        const val POLL_INTERVAL_MS = 500L
        const val SAVE_INTERVAL_MS = 4_000L

        fun newScope() = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }
}
