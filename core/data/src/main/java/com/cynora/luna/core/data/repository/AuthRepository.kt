package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.LunaUser
import kotlinx.coroutines.flow.Flow

/** Message of the failure returned by deleteAccount() when Firebase needs a fresh sign-in first. */
const val ERROR_RECENT_LOGIN_REQUIRED = "RECENT_LOGIN_REQUIRED"

interface AuthRepository {
    val currentUser: Flow<LunaUser?>
    fun getCurrentUser(): LunaUser?
    suspend fun signInWithGoogle(idToken: String): Result<LunaUser>
    suspend fun signOut()

    /**
     * Deletes the user's community posts and the Firebase account, then clears local preferences.
     * Fails with message [ERROR_RECENT_LOGIN_REQUIRED] when the user has to sign in again first.
     */
    suspend fun deleteAccount(): Result<Unit>
}

