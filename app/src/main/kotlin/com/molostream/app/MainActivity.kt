package com.molostream.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.molostream.app.navigation.MoloNavHost
import com.molostream.core.designsystem.theme.MoloStreamTheme

/** Single-activity host for the Compose UI. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Cold-start splash; hands off to the Compose UI on first frame.
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { MoloStreamTheme { MoloNavHost() } }
    }
}
