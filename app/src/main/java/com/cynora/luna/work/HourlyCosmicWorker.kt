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
import com.cynora.luna.core.data.repository.HourlyCosmicRepository
import com.cynora.luna.core.database.datastore.UserPreferencesDataStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.Calendar

@HiltWorker
class HourlyCosmicWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val cosmicRepository: HourlyCosmicRepository,
    private val preferencesDataStore: UserPreferencesDataStore
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val hourlyEnabled = preferencesDataStore.areHourlyNotificationsEnabled().first()
            if (!hourlyEnabled) {
                Timber.d("Hourly notifications disabled by user preference")
                return Result.success()
            }

            val quietHoursEnabled = preferencesDataStore.isQuietHoursEnabled().first()
            val nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

            // Quiet hours: 22:00 (10 PM) to 09:00 (9 AM)
            if (quietHoursEnabled && (nowHour >= 22 || nowHour < 9)) {
                Timber.d("Quiet hours active ($nowHour:00). Suppressing hourly pulse.")
                return Result.success()
            }

            sendHourlyPulseNotification()
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "HourlyCosmicWorker failed")
            Result.retry()
        }
    }

    private fun sendHourlyPulseNotification() {
        val pulse = cosmicRepository.getHourlyPulse()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(pulse.title)
            .setContentText(pulse.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(pulse.message))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val WORK_NAME = "luna_hourly_cosmic_work"
        const val CHANNEL_ID = "luna_hourly_cosmic"
        const val NOTIFICATION_ID = 2002
    }
}
