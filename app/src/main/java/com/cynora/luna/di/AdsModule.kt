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
    val bannerAdUnitId: String,
    val interstitialAdUnitId: String
)

@Module
@InstallIn(SingletonComponent::class)
object AdsModule {

    @Provides
    @Singleton
    fun provideAdConfig(): AdConfig {
        // Debug builds always use Google's public test units so we never click our own live ads.
        // Release builds use the Cynora AdMob units (publisher pub-4674211063760769).
        return if (BuildConfig.DEBUG) {
            AdConfig(
                nativeAdUnitId = "ca-app-pub-3940256099942544/2247696110",
                rewardedAdUnitId = "ca-app-pub-3940256099942544/5224354917",
                bannerAdUnitId = "ca-app-pub-3940256099942544/9214589741",
                interstitialAdUnitId = "ca-app-pub-3940256099942544/1033173712"
            )
        } else {
            AdConfig(
                nativeAdUnitId = "ca-app-pub-4674211063760769/7535452964",
                rewardedAdUnitId = "ca-app-pub-4674211063760769/2737140596",
                bannerAdUnitId = "ca-app-pub-4674211063760769/6325031817",
                interstitialAdUnitId = "ca-app-pub-4674211063760769/1424058928"
            )
        }
    }
}
