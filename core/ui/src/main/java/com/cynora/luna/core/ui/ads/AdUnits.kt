package com.cynora.luna.core.ui.ads

/**
 * Ad unit ids used by the ad components in this module.
 *
 * Defaults are the real Cynora AdMob units; the app module overwrites them once at startup
 * (LunaApplication.onCreate) with the ids from AdConfig, which are the same units.
 */
object AdUnits {
    @Volatile var native: String = "ca-app-pub-4674211063760769/7535452964"
    @Volatile var rewarded: String = "ca-app-pub-4674211063760769/2737140596"
    @Volatile var banner: String = "ca-app-pub-4674211063760769/6325031817"
    @Volatile var interstitial: String = "ca-app-pub-4674211063760769/1424058928"
}
