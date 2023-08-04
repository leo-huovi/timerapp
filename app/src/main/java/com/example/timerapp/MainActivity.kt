package com.example.timerapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startAlarm_10(view: View?) {
        val delay: Int = 10 * 60 * 1000 // 10 minutes in milliseconds
        val alarm_time: Long = System.currentTimeMillis() + delay
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent)
        Toast.makeText(this, "Alarm set for 10 minutes later.", Toast.LENGTH_SHORT).show()
    }
    fun startAlarm_15(view: View?) {
        val delay: Int = 15 * 60 * 1000 // 10 minutes in milliseconds
        val alarm_time: Long = System.currentTimeMillis() + delay
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm_time.toLong(), pendingIntent)
        Toast.makeText(this, "Alarm set for 15 minutes later.", Toast.LENGTH_SHORT).show()
    }
}