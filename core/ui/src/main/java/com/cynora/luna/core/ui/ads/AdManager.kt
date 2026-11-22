package com.cynora.luna.core.ui.ads

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay
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


/**
 * True once the user's consent state allows requesting ads (UMP). Re-checks every two seconds until
 * it becomes true, because the consent form can finish after this screen is first composed.
 */
@Composable
fun rememberAdsAllowed(): Boolean {
    val context = LocalContext.current
    val allowed by produceState(initialValue = false, context) {
        while (true) {
            val ok = try {
                UserMessagingPlatform.getConsentInformation(context).canRequestAds()
            } catch (e: Exception) {
                false
            }
            value = ok
            if (ok) break
            delay(2000)
        }
    }
    return allowed
}
