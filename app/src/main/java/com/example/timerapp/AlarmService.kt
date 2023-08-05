package com.example.timerapp

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.view.View
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.timerapp.MainActivity.MySingleton

private lateinit var myTextView: TextView
class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mediaPlayer = MediaPlayer.create(
            this,
            com.example.timerapp.R.raw.alarm
        ) // Replace "your_sound_file" with the mp3 file in the res/raw folder.
        mediaPlayer!!.start()
        showNotification()
        return START_NOT_STICKY
    }

    override fun onDestroy() {


        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
        }
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