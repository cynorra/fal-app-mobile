plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("kapt")
}

// AdMob app id for release builds, set in gradle.properties (LUNA_ADMOB_APP_ID).
val googleTestAdmobAppId = "ca-app-pub-3940256099942544~3347511713"
val lunaAdmobAppId: String = (findProperty("LUNA_ADMOB_APP_ID") as String?) ?: googleTestAdmobAppId
// Device-test builds can force Google test ad units even in release mode (never for tagged releases).
val forceTestAds: Boolean = (findProperty("LUNA_FORCE_TEST_ADS") as String?) == "true"

// Never ship a release tag with Google's test app id.
gradle.taskGraph.whenReady {
    val isTagBuild = System.getenv("GITHUB_REF")?.startsWith("refs/tags/v") == true
    val buildsRelease = allTasks.any { it.name.contains("Release", ignoreCase = true) && it.project == project }
    if (isTagBuild && buildsRelease && forceTestAds) {
        throw GradleException("LUNA_FORCE_TEST_ADS must not be set for a tagged release build.")
    }
    if (isTagBuild && buildsRelease && lunaAdmobAppId == googleTestAdmobAppId) {
        throw GradleException("LUNA_ADMOB_APP_ID is still Google's test app id. Set the real AdMob app id in gradle.properties before tagging a release.")
    }
}

android {
    namespace = "com.cynora.luna"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cynora.luna"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
        
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64")
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(project.property("LUNA_STORE_FILE") as String)
            storePassword = project.property("LUNA_STORE_PASSWORD") as String
            keyAlias = project.property("LUNA_KEY_ALIAS") as String
            keyPassword = project.property("LUNA_KEY_PASSWORD") as String
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            // Google's public test app id: debug builds never serve live ads.
            manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("boolean", "FORCE_TEST_ADS", "true")
        }
        
        release {
            manifestPlaceholders["admobAppId"] = lunaAdmobAppId
            buildConfigField("boolean", "FORCE_TEST_ADS", forceTestAds.toString())
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = false
        }
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    // Core & Feature Modüller
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:utils"))
    implementation(project(":feature:home"))
    implementation(project(":feature:fortune"))
    implementation(project(":feature:social"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:onboarding"))

    // Room (DatabaseModule builds the database directly)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-installations-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // Google Sign-In & Credential Manager
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    // AdMob & UMP (GDPR/KVKK)
    implementation("com.google.android.gms:play-services-ads:23.2.0")
    implementation("com.google.android.ump:user-messaging-platform:3.0.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.startup:startup-runtime:1.1.1")

    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Logging & DateTime
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.13")

    // Test
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("io.mockk:mockk:1.13.11")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

kapt {
    correctErrorTypes = true
}
