package com.cynora.luna.core.data.repository

import com.cynora.luna.core.data.model.LunaUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<LunaUser?>
    fun getCurrentUser(): LunaUser?
    suspend fun signInWithGoogle(idToken: String): Result<LunaUser>
    suspend fun signOut()
}

