package com.cynora.luna.core.database.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "luna_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    private val gson = Gson()

    companion object {
        val SEEN_FORTUNE_IDS_KEY = stringPreferencesKey("seen_fortune_ids_json")
        val USER_ZODIAC_KEY = stringPreferencesKey("user_zodiac")
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("onboarding_complete")
        val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        val HOURLY_NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("hourly_notifications_enabled")
        val QUIET_HOURS_ENABLED_KEY = booleanPreferencesKey("quiet_hours_enabled")
        val USER_SUN_SIGN_KEY = stringPreferencesKey("user_sun_sign")
        val USER_MOON_SIGN_KEY = stringPreferencesKey("user_moon_sign")
        val USER_RISING_SIGN_KEY = stringPreferencesKey("user_rising_sign")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val DARK_MODE_ENABLED_KEY = booleanPreferencesKey("dark_mode_enabled")
        val FORTUNE_COUNT_KEY = intPreferencesKey("fortune_count")
        val STREAK_COUNT_KEY = intPreferencesKey("streak_count")
        val LAST_FORTUNE_DATE_KEY = stringPreferencesKey("last_fortune_date")
    }

    // --- Seen List (JSON ile sıralama korunur) ---
    fun getSeenFortuneIds(): Flow<Set<String>> {
        return dataStore.data.map { prefs ->
            val json = prefs[SEEN_FORTUNE_IDS_KEY] ?: "[]"
            try {
                val type = object : TypeToken<List<String>>() {}.type
                val list: List<String> = gson.fromJson(json, type)
                list.toSet()
            } catch (e: Exception) {
                emptySet()
            }
        }
    }

    suspend fun updateSeenFortuneIds(fortuneId: String) {
        dataStore.edit { prefs ->
            val currentJson = prefs[SEEN_FORTUNE_IDS_KEY] ?: "[]"
            val current: MutableList<String> = try {
                val type = object : TypeToken<List<String>>() {}.type
                gson.fromJson(currentJson, type)
            } catch (e: Exception) {
                mutableListOf()
            }
            
            // Eski varsa kaldır (en sona taşımak için)
            current.remove(fortuneId)
            current.add(fortuneId)
            
            // Limit: 500 fal. Aşılırsa eski yarısını sil
            if (current.size > 500) {
                val trimmed = current.takeLast(250)
                current.clear()
                current.addAll(trimmed)
            }
            
            prefs[SEEN_FORTUNE_IDS_KEY] = gson.toJson(current)
        }
    }

    suspend fun resetSeenFortuneIds() {
        dataStore.edit { it[SEEN_FORTUNE_IDS_KEY] = "[]" }
    }

    // --- User ID (kalıcı kimlik) ---
    suspend fun getOrCreateUserId(): String {
        var result = ""
        dataStore.edit { prefs ->
            val existing = prefs[USER_ID_KEY]
            if (existing != null) {
                result = existing
            } else {
                val generated = java.util.UUID.randomUUID().toString()
                prefs[USER_ID_KEY] = generated
                result = generated
            }
        }
        return result
    }

    suspend fun saveUserId(userId: String) {
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    fun getUserId(): Flow<String?> {
        return dataStore.data.map { it[USER_ID_KEY] }
    }

    // --- Zodiac ---
    suspend fun saveUserZodiac(zodiacName: String) {
        dataStore.edit { it[USER_ZODIAC_KEY] = zodiacName }
    }

    fun getUserZodiac(): Flow<String?> {
        return dataStore.data.map { it[USER_ZODIAC_KEY] }
    }

    // --- Onboarding ---
    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETE_KEY] = complete }
    }

    fun isOnboardingComplete(): Flow<Boolean> {
        return dataStore.data.map { it[ONBOARDING_COMPLETE_KEY] ?: false }
    }

    // --- Notifications ---
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_ENABLED_KEY] = enabled }
    }

    fun areNotificationsEnabled(): Flow<Boolean> {
        return dataStore.data.map { it[NOTIFICATIONS_ENABLED_KEY] ?: true }
    }

    suspend fun setHourlyNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[HOURLY_NOTIFICATIONS_ENABLED_KEY] = enabled }
    }

    fun areHourlyNotificationsEnabled(): Flow<Boolean> {
        return dataStore.data.map { it[HOURLY_NOTIFICATIONS_ENABLED_KEY] ?: true }
    }

    suspend fun setQuietHoursEnabled(enabled: Boolean) {
        dataStore.edit { it[QUIET_HOURS_ENABLED_KEY] = enabled }
    }

    fun isQuietHoursEnabled(): Flow<Boolean> {
        return dataStore.data.map { it[QUIET_HOURS_ENABLED_KEY] ?: true }
    }

    // --- Big 3 & Name ---
    suspend fun saveBigThree(sun: String, moon: String, rising: String) {
        dataStore.edit { prefs ->
            prefs[USER_SUN_SIGN_KEY] = sun
            prefs[USER_MOON_SIGN_KEY] = moon
            prefs[USER_RISING_SIGN_KEY] = rising
        }
    }

    fun getBigThree(): Flow<Triple<String, String, String>> {
        return dataStore.data.map { prefs ->
            val sun = prefs[USER_SUN_SIGN_KEY] ?: prefs[USER_ZODIAC_KEY] ?: "Leo"
            val moon = prefs[USER_MOON_SIGN_KEY] ?: "Scorpio"
            val rising = prefs[USER_RISING_SIGN_KEY] ?: "Libra"
            Triple(sun, moon, rising)
        }
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit { it[USER_NAME_KEY] = name }
    }

    fun getUserName(): Flow<String> {
        return dataStore.data.map { it[USER_NAME_KEY] ?: "Seeker" }
    }

    // --- Dark Mode ---
    suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { it[DARK_MODE_ENABLED_KEY] = enabled }
    }

    fun isDarkModeEnabled(): Flow<Boolean> {
        return dataStore.data.map { it[DARK_MODE_ENABLED_KEY] ?: true }
    }

    // --- Stats ---
    suspend fun incrementFortuneCount() {
        dataStore.edit { prefs ->
            val current = prefs[FORTUNE_COUNT_KEY] ?: 0
            prefs[FORTUNE_COUNT_KEY] = current + 1
        }
    }

    fun getFortuneCount(): Flow<Int> {
        return dataStore.data.map { it[FORTUNE_COUNT_KEY] ?: 0 }
    }

    fun getStreakCount(): Flow<Int> {
        return dataStore.data.map { it[STREAK_COUNT_KEY] ?: 0 }
    }

    suspend fun updateStreak(todayDate: String) {
        dataStore.edit { prefs ->
            val lastDate = prefs[LAST_FORTUNE_DATE_KEY]
            val currentStreak = prefs[STREAK_COUNT_KEY] ?: 0
            if (lastDate != todayDate) {
                prefs[STREAK_COUNT_KEY] = currentStreak + 1
                prefs[LAST_FORTUNE_DATE_KEY] = todayDate
            }
        }
    }
}
