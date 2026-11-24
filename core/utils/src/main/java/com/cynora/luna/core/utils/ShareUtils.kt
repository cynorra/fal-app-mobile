package com.cynora.luna.core.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.Random

/**
 * Global share helper.
 * Every outgoing link carries a Play install referrer, so shares and invites can be told apart.
 */
object ShareUtils {

    private const val PLAY_URL = "https://play.google.com/store/apps/details?id=com.cynora.luna"

    /** Play link with an install referrer: utm_source=luna_share|luna_invite. */
    fun playLink(source: String, medium: String, content: String? = null): String {
        val referrer = buildString {
            append("utm_source=").append(source)
            append("&utm_medium=").append(medium)
            if (content != null) append("&utm_content=").append(content)
        }
        return PLAY_URL + "&referrer=" + java.net.URLEncoder.encode(referrer, "UTF-8")
    }

    fun shareFortune(
        context: Context,
        fortuneText: String,
        dailyIntention: String,
        fortuneType: String,
        shareImage: Bitmap? = null
    ) {
        val image = shareImage ?: renderCard(
            title = fortuneType,
            body = fortuneText,
            footer = dailyIntention.takeIf { it.isNotBlank() }?.let { context.getString(R.string.ui_share_intention_prefix, it) },
            ctaText = context.getString(R.string.ui_share_cta)
        )
        val link = playLink("luna_share", fortuneType.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_'))
        val shareText = context.getString(R.string.ui_share_fortune_text, fortuneType, link)
        send(context, shareText, image, "luna_share.png", context.getString(R.string.ui_share_chooser))
    }

    /** Free-form card: compatibility result, birth chart, daily card... */
    fun shareCard(context: Context, title: String, body: String, footer: String?, medium: String) {
        val image = renderCard(title, body, footer, context.getString(R.string.ui_share_cta))
        val link = playLink("luna_share", medium)
        send(context, context.getString(R.string.ui_share_card_text, title, link), image, "luna_card.png", context.getString(R.string.ui_share_chooser))
    }

    fun shareIntention(context: Context, intention: String) {
        val image = renderCard(context.getString(R.string.ui_share_intention_card_title), intention, null, context.getString(R.string.ui_share_cta))
        val link = playLink("luna_share", "intention")
        send(context, context.getString(R.string.ui_share_intention_text, link), image, "luna_intention.png", context.getString(R.string.ui_share_intention_chooser))
    }

    fun shareInvite(context: Context, code: String) {
        val link = playLink("luna_invite", "share", code)
        val text = context.getString(R.string.ui_share_invite_text, link)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(
            Intent.createChooser(intent, context.getString(R.string.ui_share_invite_chooser)).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        )
    }

    private fun send(context: Context, text: String, image: Bitmap?, fileName: String, chooserTitle: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = if (image != null) "image/png" else "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            image?.let { bitmap ->
                putExtra(Intent.EXTRA_STREAM, saveBitmapToCache(context, bitmap, fileName))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
        context.startActivity(
            Intent.createChooser(intent, chooserTitle).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        )
    }

    /** Branded 1080x1350 share card: night gradient, gold accents, wrapped text. */
    fun renderCard(title: String, body: String, footer: String?, ctaText: String = "Get Luna on Google Play"): Bitmap {
        val w = 1080
        val h = 1350
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)

        val bg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(),
                intArrayOf(Color.parseColor("#0B0820"), Color.parseColor("#2A1768"), Color.parseColor("#0B0820")),
                null, Shader.TileMode.CLAMP)
        }
        c.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bg)

        val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(w * 0.82f, h * 0.12f, 520f,
                Color.argb(110, 212, 175, 55), Color.TRANSPARENT, Shader.TileMode.CLAMP)
        }
        c.drawRect(0f, 0f, w.toFloat(), h.toFloat(), glow)

        val star = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
        val rnd = Random(7)
        repeat(70) {
            star.alpha = 60 + rnd.nextInt(150)
            c.drawCircle(rnd.nextFloat() * w, rnd.nextFloat() * h, 1f + rnd.nextFloat() * 2.4f, star)
        }

        val gold = Color.parseColor("#F3D77E")
        val pad = 96f

        val brand = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = gold; textSize = 44f; typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            letterSpacing = 0.25f
        }
        c.drawText("LUNA", pad, 150f, brand)

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE; textSize = 64f; typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
        val titleLayout = StaticLayout.Builder.obtain(title, 0, title.length, titlePaint, (w - 2 * pad).toInt())
            .setMaxLines(2).setEllipsize(TextUtils.TruncateAt.END).build()
        c.save(); c.translate(pad, 230f); titleLayout.draw(c); c.restore()

        val rule = Paint().apply { color = gold; alpha = 160; strokeWidth = 3f }
        val ruleY = 230f + titleLayout.height + 36f
        c.drawLine(pad, ruleY, pad + 160f, ruleY, rule)

        val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(235, 255, 255, 255); textSize = 46f
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        }
        val maxBodyLines = 14
        val bodyLayout = StaticLayout.Builder.obtain(body, 0, body.length, bodyPaint, (w - 2 * pad).toInt())
            .setLineSpacing(10f, 1f).setMaxLines(maxBodyLines).setEllipsize(TextUtils.TruncateAt.END)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL).build()
        c.save(); c.translate(pad, ruleY + 48f); bodyLayout.draw(c); c.restore()

        if (!footer.isNullOrBlank()) {
            val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = gold; textSize = 38f; typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            }
            val fl = StaticLayout.Builder.obtain(footer, 0, footer.length, footerPaint, (w - 2 * pad).toInt())
                .setMaxLines(2).setEllipsize(TextUtils.TruncateAt.END).build()
            c.save(); c.translate(pad, h - 250f - fl.height); fl.draw(c); c.restore()
        }

        val cta = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(200, 255, 255, 255); textSize = 36f; typeface = Typeface.SANS_SERIF
        }
        c.drawText(ctaText, pad, h - 110f, cta)
        return bmp
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
