package com.example.timerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.PowerManager


class AlarmReceiver : BroadcastReceiver() {
    private var alarmPlayer: MediaPlayer? = null
    override fun onReceive(context: Context, intent: Intent) {

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        var wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationManagerService")
        wakeLock.acquire()
        // Create a new MediaPlayer instance
        alarmPlayer = MediaPlayer.create(context, R.raw.alarm)

        // Set a listener to release the MediaPlayer resources when the alarm finishes playing
        alarmPlayer!!.setOnCompletionListener { stopAlarm() }

        // Start playing the alarm sound
        alarmPlayer!!.start()

        wakeLock.release()
    }

    private fun stopAlarm() {
        // Stop and release the MediaPlayer resources
        if (alarmPlayer != null) {
            alarmPlayer!!.stop()
            alarmPlayer!!.release()
            alarmPlayer = null
        }
    }
}