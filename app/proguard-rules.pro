# Luna ProGuard Rules

# Kotlin
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.reflect.jvm.internal.**
-keep class kotlin.reflect.jvm.internal.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Hilt / Dagger
-dontwarn dagger.hilt.internal.aggregatedroot.codegen.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }

# Firestore DTOs
-keep class com.cynora.luna.core.data.remote.** { *; }
-keepclassmembers class com.cynora.luna.core.data.remote.** {
    <init>(...);
    *;
}

# Domain Models
-keep class com.cynora.luna.core.data.model.** { *; }

# Repository Data Classes
-keep class com.cynora.luna.core.data.repository.** { *; }

# Enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Compose
-dontwarn androidx.compose.**

# AdMob
-keep public class com.google.android.gms.ads.** { public *; }
-keep public class com.google.ads.** { public *; }

# Timber
-dontwarn org.jetbrains.annotations.**

# Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

# Crashlytics
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# WorkManager + HiltWorker
-keep class com.cynora.luna.work.** { *; }
-keep class androidx.work.** { *; }
-keep class androidx.hilt.work.** { *; }
-keep @androidx.hilt.work.HiltWorker class * { *; }

# General
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

# Seed fortunes are parsed with Gson reflection (SeedDataWrapper, SeedFortuneDto).
# Without this R8 strips/renames them and no fortune is ever loaded in release builds.
-keep class com.cynora.luna.core.data.seed.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
