package com.cynora.luna.core.ui.ads

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cynora.luna.core.ui.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import timber.log.Timber

@Composable
fun NativeAdvancedAd(
    adUnitId: String = AdUnits.native,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasLoaded by remember { mutableStateOf(false) }
    
    // ✅ Consent kontrolü
    val canShowAds = rememberCanShowAds()
    
    LaunchedEffect(adUnitId, canShowAds) {
        if (!canShowAds || hasLoaded) return@LaunchedEffect
        hasLoaded = true
        
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad ->
                nativeAd = ad
                isLoading = false
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false
                    Timber.w("Ad load failed: ${error.message}")
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()
        
        // ✅ Basit AdRequest (AdMobAdapter kaldırıldı)
        adLoader.loadAd(AdRequest.Builder().build())
    }
    
    if (!canShowAds) {
        Box(modifier = modifier.fillMaxWidth().height(320.dp))
        return
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        when {
            isLoading -> ShimmerPlaceholder()
            nativeAd != null -> AndroidView(
                factory = { ctx ->
                    val adView = View.inflate(ctx, R.layout.native_ad_view, null) as NativeAdView
                    nativeAd?.let { ad -> populateNativeAdView(ad, adView) }
                    adView
                },
                modifier = Modifier.fillMaxSize()
            )
            else -> Box(modifier = Modifier.fillMaxSize())
        }
    }
    
    DisposableEffect(Unit) {
        onDispose { nativeAd?.destroy() }
    }
}

private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    adView.bodyView = adView.findViewById(R.id.ad_body)
    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
    adView.iconView = adView.findViewById(R.id.ad_icon)
    adView.mediaView = adView.findViewById(R.id.ad_media)
    adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

    (adView.headlineView as? TextView)?.text = nativeAd.headline
    (adView.bodyView as? TextView)?.text = nativeAd.body
    (adView.callToActionView as? Button)?.text = nativeAd.callToAction
    (adView.iconView as? ImageView)?.setImageDrawable(nativeAd.icon?.drawable)
    (adView.advertiserView as? TextView)?.text = nativeAd.advertiser

    adView.setNativeAd(nativeAd)
}

@Composable
private fun ShimmerPlaceholder() {
    Card(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(0.7f).height(20.dp)
                    .background(Color(0xFF2C2C2C), RoundedCornerShape(4.dp))
            )
            Box(
                modifier = Modifier.fillMaxWidth().height(60.dp)
                    .background(Color(0xFF2C2C2C), RoundedCornerShape(4.dp))
            )
            Box(
                modifier = Modifier.fillMaxWidth(0.4f).height(40.dp)
                    .background(Color(0xFF2C2C2C), RoundedCornerShape(8.dp))
            )
        }
    }
}
