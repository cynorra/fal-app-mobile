package com.cynora.luna.core.ui.ads

/**
 * Ad unit ids used by the ad components in this module.
 *
 * The defaults are Google's public test ids. The app module overwrites them once at startup
 * (LunaApplication.onCreate) with the ids from AdConfig, which are test ids in debug builds and
 * the real Cynora AdMob ids in release builds.
 */
object AdUnits {
    const val TEST_NATIVE = "ca-app-pub-3940256099942544/2247696110"
    const val TEST_REWARDED = "ca-app-pub-3940256099942544/5224354917"
    const val TEST_BANNER = "ca-app-pub-3940256099942544/9214589741"
    const val TEST_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"

    @Volatile var native: String = TEST_NATIVE
    @Volatile var rewarded: String = TEST_REWARDED
    @Volatile var banner: String = TEST_BANNER
    @Volatile var interstitial: String = TEST_INTERSTITIAL
}
