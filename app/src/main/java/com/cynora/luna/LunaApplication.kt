package com.cynora.luna

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.cynora.luna.core.data.seed.SeedDataLoader
import com.cynora.luna.core.ui.ads.AdUnits
import com.cynora.luna.di.AdConfig
import com.cynora.luna.work.WorkScheduler
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.RequestConfiguration
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

    @Inject
    lateinit var inviteRepository: com.cynora.luna.core.data.repository.InviteRepository

    @Inject
    lateinit var adConfig: AdConfig

    private val appScope = CoroutineScope(Job() + Dispatchers.IO)

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
                    .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                    .build()
            )
        }

        AdUnits.native = adConfig.nativeAdUnitId
        AdUnits.rewarded = adConfig.rewardedAdUnitId
        AdUnits.banner = adConfig.bannerAdUnitId
        AdUnits.interstitial = adConfig.interstitialAdUnitId

        createNotificationChannels()

        appScope.launch {
            try {
                seedDataLoader.loadSeedDataIfNeeded()
            } catch (e: Exception) {
                Timber.e(e, "Seed data load failed")
            }
        }

        // Friend invite: read the Play install referrer once after install and register the join
        appScope.launch {
            try {
                inviteRepository.claimInviteIfAny()
            } catch (e: Exception) {
                Timber.w(e, "Invite claim failed")
            }
        }

        WorkScheduler.scheduleDailyFortune(this, 8, 30)
        WorkScheduler.scheduleHourlyCosmic(this)
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
                    "luna_hourly_cosmic",
                    "Hourly Cosmic Pulse",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Hourly planetary hours, affirmations, and lucky energy windows"
                    enableLights(true)
                    enableVibration(true)
                    lightColor = 0xFF9D8DF1.toInt()
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
