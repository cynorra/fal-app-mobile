package com.cynora.luna

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import com.cynora.luna.core.ui.theme.LunaTheme
import com.cynora.luna.navigation.LunaNavHost
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: UserPreferencesDataStore

    private lateinit var consentInfo: ConsentInformation

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        initConsent()

        setContent {
            val isDark by dataStore.isDarkModeEnabled()
                .collectAsStateWithLifecycle(initialValue = true)

            LunaTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LunaNavHost()
                }
            }
        }
    }

    private fun initAdsIfAllowed() {
        val info = UserMessagingPlatform.getConsentInformation(this)
        if (info.canRequestAds()) {
            MobileAds.initialize(this) { status ->
                Timber.d("AdMob initialized: $status")
            }
        }
    }

    private fun initConsent() {
        val info = UserMessagingPlatform.getConsentInformation(this)
        consentInfo = info

        if (info.canRequestAds()) {
            initAdsIfAllowed()
        }

        info.requestConsentInfoUpdate(
            this,
            ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false)
                .build(),
            {
                if (info.isConsentFormAvailable) {
                    loadConsentForm()
                }
            },
            { error ->
                Timber.e("Consent update failed: ${error.message}")
            }
        )
    }

    private fun loadConsentForm() {
        UserMessagingPlatform.loadConsentForm(
            this,
            { form ->
                if (consentInfo.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    form.show(this) { error ->
                        if (error != null) {
                            Timber.e("Consent form show failed: ${error.message}")
                        }
                        initAdsIfAllowed()
                    }
                } else {
                    initAdsIfAllowed()
                }
            },
            { error ->
                Timber.e("Consent form load failed: ${error.message}")
            }
        )
    }
}
