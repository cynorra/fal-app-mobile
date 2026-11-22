package com.cynora.luna.core.ui.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import android.util.Log
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdaptiveBannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val owner = LocalLifecycleOwner.current

    // Adaptive banners must be requested for the width they are drawn in. Cap it for tablets.
    val widthDp = minOf(configuration.screenWidthDp, 728)
    val adSize = remember(widthDp) {
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
    }
    // AdSize.height / width are already in dp: do not convert them as pixels (that clipped the ad)
    val heightDp = adSize.height.dp
    val widthAdDp = adSize.width.dp
    val holder = remember { mutableStateOf<AdView?>(null) }
    var failed by remember { mutableStateOf(false) }

    DisposableEffect(owner, adUnitId, adSize) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) { holder.value?.resume() }
            override fun onPause(owner: LifecycleOwner) { holder.value?.pause() }
            override fun onDestroy(owner: LifecycleOwner) {
                holder.value?.destroy()
                holder.value = null
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose {
            owner.lifecycle.removeObserver(observer)
            holder.value?.destroy()
            holder.value = null
        }
    }

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        key(adUnitId, adSize) {
            AndroidView(
                factory = { ctx ->
                    AdView(ctx).apply {
                        this.adUnitId = adUnitId
                        setAdSize(adSize)
                        adListener = object : AdListener() {
                            override fun onAdLoaded() {
                                failed = false
                                Log.d(LOG_TAG, "Banner loaded")
                            }

                            override fun onAdFailedToLoad(error: LoadAdError) {
                                // Collapse the empty strip when there is no fill
                                failed = true
                                Log.w(LOG_TAG, "Banner failed to load: code=${error.code} ${error.message}")
                            }
                        }
                        holder.value = this
                        loadAd(AdRequest.Builder().build())
                    }
                },
                modifier = Modifier
                    .width(widthAdDp)
                    .height(if (failed) 0.dp else heightDp)
            )
        }
    }
}

private const val LOG_TAG = "LunaAds"
