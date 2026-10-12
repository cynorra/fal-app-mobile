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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dataStore: UserPreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

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
}
