package com.example.timerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class AlarmCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Enqueue the ResetWorker after the alarm completes
        val resetWorkRequest = OneTimeWorkRequest.Builder(ResetWorker::class.java)
            .setInitialDelay(1, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueue(resetWorkRequest)
    }
}
