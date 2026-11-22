package com.cynora.luna.work

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cynora.luna.MainActivity
import com.cynora.luna.R
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.Calendar

/**
 * The morning nudge. Respects the user's switch, stays quiet when today's reading is already done,
 * and opens straight onto the card of the day.
 */
@HiltWorker
class DailyFortuneWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dataStore: UserPreferencesDataStore
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            if (!dataStore.areNotificationsEnabled().first()) {
                Timber.d("Daily notification disabled by user preference")
                return Result.success()
            }
            if (dataStore.hasReadToday()) return Result.success()

            val streak = dataStore.getActiveStreak()
            val sign = dataStore.getUserZodiac().first()
                ?.lowercase()?.replaceFirstChar { it.uppercase() }
            val (title, message) = buildCopy(streak, sign)
            sendNotification(title, message)
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "DailyFortuneWorker failed")
            Result.retry()
        }
    }

    private fun buildCopy(streak: Int, sign: String?): Pair<String, String> {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val who = sign ?: "Today"
        if (streak >= 2) {
            return when (dayOfYear % 2) {
                0 -> "Day ${streak + 1} is waiting for you" to
                    "Your $streak-day ritual is still glowing. Come and see today's card, it takes under a minute."
                else -> "Keep your $streak-day streak alive" to
                    "A quiet minute with today's reading keeps your ritual going. Come take a look."
            }
        }
        val options = listOf(
            "Your card for today is ready" to
                "Come take a look. A small moment of reflection to start the day.",
            "$who, the sky has a message for you" to
                "Come and see what today's reading holds. It only takes a minute.",
            "A new day, a fresh reading" to
                "Come take a look at your daily card and set your intention.",
            "Your daily intention is waiting" to
                "Take a calm minute for yourself. Come and see today's card."
        )
        return options[dayOfYear % options.size]
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MainActivity.EXTRA_ROUTE, "daily_card")
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val WORK_NAME = "luna_daily_fortune_work"
        const val CHANNEL_ID = "luna_daily_fortune"
        const val NOTIFICATION_ID = 1001
    }
}
