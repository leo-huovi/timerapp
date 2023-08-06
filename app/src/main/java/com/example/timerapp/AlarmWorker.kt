    package com.example.timerapp

    import android.app.NotificationChannel
    import android.app.NotificationManager
    import android.content.Context
    import android.content.Intent
    import android.os.Build
    import android.os.PowerManager
    import androidx.core.content.ContextCompat
    import androidx.work.Worker
    import androidx.work.WorkerParameters

    class MyWorker(context: Context, workerParams: WorkerParameters) :
        Worker(context, workerParams) {

        override fun doWork(): Result {
            val powerManager = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wl = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "myapp:alarmwakeclock"
            )
            // Acquire the lock
            wl.acquire()

            // Create a notification channel for Foreground Service (required for API level 26 and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "timer_channel"
                val channelName = "Timer Service Channel"
                val notificationManager =
                    applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            // Release the lock
            wl.release()

            // Start the Foreground Service
            val serviceIntent = Intent(applicationContext, AlarmService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(applicationContext, serviceIntent)
            } else {
                applicationContext.startService(serviceIntent)
            }

            return Result.success()
        }
    }
