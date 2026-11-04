package com.cynora.luna.core.data.model

data class LunaUser(
    val uid: String,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null
)

