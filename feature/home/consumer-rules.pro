################################################################################
#  :feature:home — consumer R8 rules
#
#  Published with the module and merged into the consuming app's R8 run, so the
#  catalog DTOs stay deserializable wherever this feature is reused.
#
#  Home owns the @Serializable wire models (CatalogDto / MovieDto) decoded from
#  assets/catalog.json. The canonical kotlinx.serialization keep set below is
#  program-wide (R8 dedups it against the copy in the app module), guaranteeing
#  the generated companions + serializers survive full-mode shrinking.
################################################################################

-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Keep the `Companion` field of every @Serializable class.
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

# Keep `INSTANCE` + `serializer()` of @Serializable objects / data objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

-dontwarn kotlinx.serialization.**
