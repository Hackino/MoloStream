################################################################################
#  :feature:player — consumer R8 rules
#
#  These rules are published with the module and merged into the R8 run of any
#  app that depends on it, so the player's reflection surface stays correct
#  wherever it is consumed.
#
#  Covers the Media3 / ExoPlayer / HLS stack and the Google IMA ad SDK that
#  back ExoPlayerController.
################################################################################


# -----------------------------------------------------------------------------
#  Media3 / ExoPlayer / HLS
#
#  Media3 ships its own consumer rules inside each AAR (they keep the
#  reflectively-instantiated extractors, renderers and DataSource factories),
#  so we deliberately do NOT blanket `-keep class androidx.media3.** { *; }` —
#  that would defeat shrinking of a large dependency. We only silence warnings
#  for the optional decoders/extensions we don't bundle but that are referenced
#  by the default renderer/extractor factories.
# -----------------------------------------------------------------------------
-dontwarn androidx.media3.**


# -----------------------------------------------------------------------------
#  Google IMA SDK  (transitive via androidx.media3:media3-exoplayer-ima)
#
#  The IMA SDK loads its ad-rendering and JS-bridge classes reflectively and is
#  not safe to obfuscate — Google publishes this exact keep requirement. Its
#  play-services transitives are referenced but partly unused, so warn-suppress
#  them rather than keep them.
# -----------------------------------------------------------------------------
-keep class com.google.ads.interactivemedia.** { *; }
-keep interface com.google.ads.interactivemedia.** { *; }
-dontwarn com.google.ads.interactivemedia.**
-dontwarn com.google.android.gms.**
-dontwarn com.google.android.libraries.**
