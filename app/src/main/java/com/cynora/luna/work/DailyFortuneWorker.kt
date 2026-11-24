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
        val who = sign ?: context.getString(R.string.ui_app_today)
        if (streak >= 2) {
            return when (dayOfYear % 2) {
                0 -> context.getString(R.string.ui_app_nudge_streak_a_title, streak + 1) to
                    context.getString(R.string.ui_app_nudge_streak_a_body, streak)
                else -> context.getString(R.string.ui_app_nudge_streak_b_title, streak) to
                    context.getString(R.string.ui_app_nudge_streak_b_body)
            }
        }
        val options = listOf(
            context.getString(R.string.ui_app_nudge_1_title) to
                context.getString(R.string.ui_app_nudge_1_body),
            context.getString(R.string.ui_app_nudge_2_title, who) to
                context.getString(R.string.ui_app_nudge_2_body),
            context.getString(R.string.ui_app_nudge_3_title) to
                context.getString(R.string.ui_app_nudge_3_body),
            context.getString(R.string.ui_app_nudge_4_title) to
                context.getString(R.string.ui_app_nudge_4_body)
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
