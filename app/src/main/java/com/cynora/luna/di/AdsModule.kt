package com.cynora.luna.di

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
        // Real Cynora AdMob units (publisher pub-4674211063760769) in every build type;
        // Google's test units are intentionally not used anywhere.
        return AdConfig(
            nativeAdUnitId = "ca-app-pub-4674211063760769/7535452964",
            rewardedAdUnitId = "ca-app-pub-4674211063760769/2737140596",
            bannerAdUnitId = "ca-app-pub-4674211063760769/6325031817",
            interstitialAdUnitId = "ca-app-pub-4674211063760769/1424058928"
        )
    }
}
