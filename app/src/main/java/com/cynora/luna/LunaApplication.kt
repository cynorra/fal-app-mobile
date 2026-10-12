package com.cynora.luna

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.cynora.luna.core.data.seed.SeedDataLoader
import com.cynora.luna.work.WorkScheduler
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class LunaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var seedDataLoader: SeedDataLoader

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val appScope = CoroutineScope(Job() + Dispatchers.IO)
    private lateinit var consentInfo: ConsentInformation

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf("EMULATOR_ID"))
                    .build()
            )
        }

        createNotificationChannels()
        initConsent()

        appScope.launch {
            try {
                seedDataLoader.loadSeedDataIfNeeded()
            } catch (e: Exception) {
                Timber.e(e, "Seed data load failed")
            }
        }

        WorkScheduler.scheduleDailyFortune(this, 9, 0)
    }

    private fun initAdsIfAllowed() {
        val info = UserMessagingPlatform.getConsentInformation(this)
        if (info.canRequestAds()) {
            MobileAds.initialize(this) { status ->
                Timber.d("AdMob initialized: $status")
            }
        }
    }

    private fun initConsent() {
        val info = UserMessagingPlatform.getConsentInformation(this)
        consentInfo = info

        if (info.canRequestAds()) {
            initAdsIfAllowed()
        }

        info.requestConsentInfoUpdate(
            this,
            ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false)
                .build(),
            {
                if (info.isConsentFormAvailable) {
                    loadConsentForm()
                }
            },
            { error ->
                Timber.e("Consent update failed: ${error.message}")
            }
        )
    }

    private fun loadConsentForm() {
        UserMessagingPlatform.loadConsentForm(
            this,
            { form ->
                if (consentInfo.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    form.show(this) { error ->
                        if (error != null) {
                            Timber.e("Consent form show failed: ${error.message}")
                        }
                        initAdsIfAllowed()
                    }
                } else {
                    initAdsIfAllowed()
                }
            },
            { error ->
                Timber.e("Consent form load failed: ${error.message}")
            }
        )
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)

            val channels = listOf(
                NotificationChannel(
                    "luna_daily_fortune",
                    "Daily Fortune",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Daily fortune readings"
                    enableLights(true)
                    enableVibration(true)
                    lightColor = 0xFFD4AF37.toInt()
                },
                NotificationChannel(
                    "luna_social",
                    "Community",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Community interactions"
                },
                NotificationChannel(
                    "luna_calendar",
                    "Mystic Calendar",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Moon phases"
                    enableLights(true)
                    lightColor = 0xFF9D76C1.toInt()
                },
                NotificationChannel(
                    "luna_special",
                    "Special Events",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Cosmic events"
                    enableVibration(true)
                }
            )

            nm.createNotificationChannels(channels)
        }
    }
}
