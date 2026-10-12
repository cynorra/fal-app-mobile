package com.cynora.luna.core.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/**
 * Global paylaşım utility'si
 * ✅ FileProvider entegrasyonu
 */
object ShareUtils {

    fun shareFortune(
        context: Context,
        fortuneText: String,
        dailyIntention: String,
        fortuneType: String,
        shareImage: Bitmap? = null
    ) {
        val shareText = buildString {
            appendLine("✨ $fortuneType by Luna ✨")
            appendLine()
            appendLine("\"$fortuneText\"")
            appendLine()
            appendLine("🌟 Daily Intention: $dailyIntention")
            appendLine()
            appendLine("— Get Luna: Your Personal Cosmic Guide")
            appendLine("https://play.google.com/store/apps/details?id=com.cynora.luna")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = if (shareImage != null) "image/*" else "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)

            shareImage?.let { bitmap ->
                val uri = saveBitmapToCache(context, bitmap, "luna_fortune.png")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }

        context.startActivity(
            Intent.createChooser(intent, "Share via").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    fun shareIntention(context: Context, intention: String) {
        val shareText = buildString {
            appendLine("🌟 Today's Intention:")
            appendLine()
            appendLine("\"$intention\"")
            appendLine()
            appendLine("— Luna")
            appendLine("https://play.google.com/store/apps/details?id=com.cynora.luna")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        context.startActivity(
            Intent.createChooser(intent, "Share intention").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap, fileName: String): android.net.Uri {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, fileName)
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
