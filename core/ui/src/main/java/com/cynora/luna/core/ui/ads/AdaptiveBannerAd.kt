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
    val density = LocalDensity.current
    val owner = LocalLifecycleOwner.current
    
    val widthDp = configuration.screenWidthDp
    val adSize = remember(widthDp) {
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
    }
    val height = with(density) { adSize.height.toDp() }
    val holder = remember { mutableStateOf<AdView?>(null) }
    
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
    
    key(adUnitId, adSize) {
        AndroidView(
            factory = { ctx ->
                AdView(ctx).apply {
                    this.adUnitId = adUnitId
                    setAdSize(adSize)
                    holder.value = this
                    loadAd(AdRequest.Builder().build())
                }
            },
            modifier = modifier.fillMaxWidth().height(height)
        )
    }
}
