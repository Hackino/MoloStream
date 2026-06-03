package com.molostream.feature.player.di

import com.molostream.feature.player.data.ExoPlayerController
import com.molostream.feature.player.domain.PlayerArgs
import com.molostream.feature.player.presentation.PlayerViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Player wiring: presentation VM ← domain controller ← data ExoPlayer impl.
 * The (args, subscribed) pair is supplied at navigation time via parametersOf.
 */
val playerModule = module {
    viewModel { params ->
        PlayerViewModel(
            ExoPlayerController(
                appContext = androidApplication(),
                args = params.get<PlayerArgs>(),
                subscribed = params.get<Boolean>(),
                getResumePosition = get(),
                saveWatchProgress = get(),
            ),
        )
    }
}
