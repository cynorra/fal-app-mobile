package com.cynora.luna.work

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            Timber.d("BootReceiver triggered with action: ${intent.action}")
            WorkScheduler.scheduleDailyFortune(context, 9, 0)
        }
    }
}
