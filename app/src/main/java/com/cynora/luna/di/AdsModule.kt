package com.cynora.luna.di

import com.cynora.luna.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

data class AdConfig(
    val nativeAdUnitId: String,
    val rewardedAdUnitId: String,
    val bannerAdUnitId: String
)

@Module
@InstallIn(SingletonComponent::class)
object AdsModule {

    @Provides
    @Singleton
    fun provideAdConfig(): AdConfig {
        val isDebug = BuildConfig.DEBUG

        return AdConfig(
            nativeAdUnitId = if (isDebug) {
                "ca-app-pub-3940256099942544/2247696110"
            } else {
                "ca-app-pub-XXXXXXXXXXXXXXXX/YYYYYYYYYY" // Production ID
            },
            rewardedAdUnitId = if (isDebug) {
                "ca-app-pub-3940256099942544/5224354917"
            } else {
                "ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ"
            },
            bannerAdUnitId = if (isDebug) {
                "ca-app-pub-3940256099942544/9214589741"
            } else {
                "ca-app-pub-XXXXXXXXXXXXXXXX/WWWWWWWWWW"
            }
        )
    }
}
