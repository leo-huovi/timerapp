package com.example.timerapp

import android.app.NotificationChannel
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.timerapp.R
import android.app.NotificationManager
import android.media.MediaPlayer
import android.os.Build
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate() {
        super.onCreate()
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "app:alarmwakeclock"
        )
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Acquire the wake lock
        wakeLock.acquire()

        mediaPlayer = MediaPlayer.create(
            this,
            R.raw.alarm
        ) // Replace "your_sound_file" with the mp3 file in the res/raw folder.
        mediaPlayer!!.start()
        showNotification()

        // Enqueue the ResetWorker with a delay of 1 second
        val resetWorkRequest = OneTimeWorkRequest.Builder(ResetWorker::class.java)
            .setInitialDelay(1, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(this).enqueue(resetWorkRequest)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }

        // Release the wake lock
        if (wakeLock.isHeld) {
            wakeLock.release()
        }

        // Send a broadcast to indicate that the alarm has completed
        val alarmCompletedIntent = Intent("com.example.timerapp.ALARM_COMPLETED")
        sendBroadcast(alarmCompletedIntent)
    }


    private fun showNotification() {
        val channelId = "alarm_channel"
        val channelName = "Alarm Channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(com.example.timerapp.R.drawable.alarm_10_minutes)
            .setContentTitle("Alarm")
            .setContentText("Time to wake up!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notification = builder.build()
        notificationManager.notify(1, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}