package com.cynora.luna.core.ui.ads

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cynora.luna.core.utils.findActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RewardedAdManager(
    private val activity: Activity,
    private val adUnitId: String = AdUnits.rewarded
) : DefaultLifecycleObserver {
    
    private var rewardedAd: RewardedAd? = null
    private var loadRetryCount = 0  // ✅ Sadece load failure için
    private val maxLoadRetries = 3
    
    private val _adState = MutableStateFlow<AdState>(AdState.Idle)
    val adState: StateFlow<AdState> = _adState.asStateFlow()

    sealed class AdState {
        object Idle : AdState()
        object Loading : AdState()
        object Loaded : AdState()
        data class Reward(val amount: Int) : AdState()
        data class Error(val message: String) : AdState()
    }

    fun loadAd() {
        if (loadRetryCount >= maxLoadRetries) {
            _adState.value = AdState.Error("Ad not available, try later")
            return
        }
        
        _adState.value = AdState.Loading

        RewardedAd.load(
            activity,
            adUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    android.util.Log.d("LunaAds", "Rewarded ad loaded")
                    loadRetryCount = 0  // ✅ Başarıda sıfırla
                    _adState.value = AdState.Loaded

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            rewardedAd = null
                            _adState.value = AdState.Idle
                            // ✅ retryCount ARTIRILMIYOR
                            loadAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            rewardedAd = null
                            _adState.value = AdState.Error("Ad failed to show")
                            loadAd()
                        }
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    android.util.Log.w("LunaAds", "Rewarded ad failed to load: code=${error.code} ${error.message}")
                    loadRetryCount++  // ✅ Sadece load failure'da artar
                    _adState.value = AdState.Error("Ad failed to load: ${error.message}")
                    rewardedAd = null
                }
            }
        )
    }

    fun showAd(onReward: (Int) -> Unit) {
        val ad = rewardedAd ?: run {
            _adState.value = AdState.Error("Ad not loaded yet")
            return
        }
        ad.show(activity) { rewardItem ->
            onReward(rewardItem.amount)
            _adState.value = AdState.Reward(rewardItem.amount)
        }
    }

    fun isAdLoaded(): Boolean = rewardedAd != null
    
    override fun onResume(owner: LifecycleOwner) {
        if (rewardedAd == null && loadRetryCount < maxLoadRetries) loadAd()
    }

    override fun onPause(owner: LifecycleOwner) {
        rewardedAd = null
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cleanup()
    }
    
    fun cleanup() {
        rewardedAd = null
        loadRetryCount = maxLoadRetries
    }
}

@Composable
fun rememberRewardedAdManager(): RewardedAdManager {
    val context = LocalContext.current
    val activity = context.findActivity()
        ?: throw IllegalStateException("Activity not found")
    
    val lifecycleOwner = LocalLifecycleOwner.current
    val manager = remember { RewardedAdManager(activity) }
    
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(manager)
        manager.loadAd()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(manager)
            manager.cleanup()
        }
    }
    
    return manager
}
