package com.cynora.luna.core.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Compose context'ten güvenli Activity bulma
 * ✅ tailrec ile optimize edildi (infinite loop riski yok)
 */
tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
