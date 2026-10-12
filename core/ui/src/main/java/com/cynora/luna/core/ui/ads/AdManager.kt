package com.cynora.luna.core.ui.ads

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import timber.log.Timber

object AdManager {
    
    fun canShowAds(context: Context): Boolean {
        return try {
            val consentInfo = UserMessagingPlatform.getConsentInformation(context)
            when (consentInfo.consentStatus) {
                ConsentInformation.ConsentStatus.OBTAINED,
                ConsentInformation.ConsentStatus.NOT_REQUIRED -> true
                else -> false
            }
        } catch (e: Exception) {
            Timber.e(e, "Consent check failed")
            true
        }
    }
    
    fun canShowPersonalizedAds(context: Context): Boolean {
        return try {
            UserMessagingPlatform.getConsentInformation(context).canRequestAds()
        } catch (e: Exception) {
            Timber.e(e, "Personalized ads check failed")
            false
        }
    }
}

@Composable
fun rememberCanShowAds(): Boolean {
    val context = LocalContext.current
    return remember { AdManager.canShowAds(context) }
}
