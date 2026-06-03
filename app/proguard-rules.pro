################################################################################
#  MoloStream — R8 / ProGuard rules (release build)
#
#  R8 runs in FULL MODE (the AGP 8+/9+ default). Every rule below is written
#  for full mode, which is stricter than legacy ProGuard: it ignores implicit
#  "keep everything referenced" assumptions, so reflection surfaces must be
#  declared explicitly.
#
#  Scope of this file (the application module):
#    • Global crash-deobfuscation attributes (apply to the whole program).
#    • App-owned reflection: kotlinx.serialization Navigation 3 keys.
#    • Third-party libraries the *app* depends on directly: coroutines,
#      Navigation 3, Koin, Coil, kotlinx.serialization runtime.
#
#  Library-owned reflection lives next to the code that needs it, so the rule
#  travels with the module if it is ever reused:
#    • feature/player/consumer-rules.pro → Media3 / ExoPlayer / HLS / IMA
#    • feature/home/consumer-rules.pro   → kotlinx.serialization catalog DTOs
#
#  Libraries that ship their own consumer rules inside their AAR are NOT
#  re-declared here (re-keeping them only bloats the APK and defeats
#  shrinking): Jetpack Compose, Room, DataStore, Lifecycle, Coil, Media3.
################################################################################


# =============================================================================
#  1. Crash readability — keep just enough metadata to deobfuscate stack traces.
#
#  R8 still renames everything; it only preserves the line/source attributes so
#  the mapping file can reverse a production stack trace. After every release,
#  upload  app/build/outputs/mapping/release/mapping.txt  to your crash
#  reporter (Play Console → "Deobfuscation files", or Crashlytics).
# =============================================================================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Metadata read at runtime by reflective libraries (serialization, Room,
# Media3) and by Kotlin itself for generics / default-argument synthetics.
-keepattributes Signature,Exceptions,InnerClasses,EnclosingMethod
-keepattributes *Annotation*,RuntimeVisibleAnnotations,AnnotationDefault,RuntimeVisibleParameterAnnotations


# =============================================================================
#  2. Kotlin runtime
#
#  kotlin-stdlib ships its own rules; we only guard the @Metadata annotation
#  (used by serialization to resolve property order) and silence warnings for
#  the compile-only JetBrains annotations.
# =============================================================================
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-dontwarn org.jetbrains.annotations.**
-dontwarn javax.annotation.**


# =============================================================================
#  3. kotlinx.coroutines
#
#  The runtime ships consumer rules; these cover the full-mode edge cases the
#  stdlib rules miss — the ServiceLoader main-dispatcher factory, the
#  volatile fields driven by atomic field updaters, and the debug-probe shim
#  absent in release.
# =============================================================================
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}
# Used only by the coroutine debugger (debug builds); never linked in release.
-dontwarn kotlinx.coroutines.debug.**
-dontwarn kotlinx.coroutines.**


# =============================================================================
#  4. kotlinx.serialization  (AUTHORITATIVE, program-wide)
#
#  Covers every @Serializable type in the app: the Navigation 3 destination
#  keys here in :app and the catalog DTOs in :feature:home. These are the
#  canonical rules from the kotlinx.serialization README, required because R8
#  full mode strips the generated companions/serializers otherwise.
#
#  The synthetic  *$$serializer  classes are reached transitively from the kept
#  `serializer()` methods, so they do not need a separate -keep.
# =============================================================================
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Keep the `Companion` field of every @Serializable class (avoids a
# getDeclaredClasses lookup for named companions).
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on the companion (default or named) of @Serializable types.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE` + `serializer()` of @Serializable objects / data objects
# (e.g. SplashKey, HomeKey).
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

-dontwarn kotlinx.serialization.**


# =============================================================================
#  5. Koin
#
#  Koin 4.x resolves dependencies through DSL lambdas, NOT reflection — the
#  concrete types are referenced directly in the module blocks, so R8 keeps
#  them on its own. No keep rules are required; this note exists so a future
#  reader does not "add Koin rules" defensively and bloat the build.
# =============================================================================
-dontwarn org.koin.**


# =============================================================================
#  6. AndroidX Navigation 3
#
#  Back-stack entries are persisted by serializing the @Serializable NavKey
#  subclasses (handled by §4). The runtime ships its own keeps for the
#  scene/entry machinery; we only silence warnings for optional APIs.
# =============================================================================
-dontwarn androidx.navigation3.**


# =============================================================================
#  7. Coil 3
#
#  Coil ships consumer rules in its artifacts. Posters load from app assets
#  (no network fetcher engine is bundled), so nothing extra is required here —
#  just silence warnings from the optional network/okio code paths.
# =============================================================================
-dontwarn coil3.**
-dontwarn okio.**


# =============================================================================
#  8. App entry points
#
#  Manifest-declared components (MoloApplication, MainActivity) are kept
#  automatically by AGP's generated manifest-keep rules, so they are NOT
#  listed here. Add app-specific keeps below only when a real reflection or
#  JNI surface is introduced.
# =============================================================================
