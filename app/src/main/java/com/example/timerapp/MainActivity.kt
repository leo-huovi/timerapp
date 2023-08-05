package com.example.timerapp

import TimerViewModel
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.timerapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {


    object MySingleton {
        var alarmOn: Boolean = false
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var timerViewModel: TimerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

        // Observe the timer LiveData and update the TextView
        timerViewModel.currentTime.observe(this, Observer { timeInMillis ->
            updateTimerText(timeInMillis)
        })

        timerViewModel.startTimer(0)


        }
        // Set a click listener on a button to start the timer with a specified time

    private fun updateTimerText(timeInMillis: Long) {
        val format = SimpleDateFormat("mm:ss", Locale.getDefault())
        val formattedTime = format.format(Date(timeInMillis))
        binding.textView.text = formattedTime
    }

    override fun onDestroy() {
        super.onDestroy()
        timerViewModel.stopTimer()
    }

    fun startAlarm_5(view: View?) {
        val delay: Int = 5 * 60 * 1000 // 10 minutes in milliseconds
        val alarm_time: Long = System.currentTimeMillis() + delay
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent)
        Toast.makeText(this, "Alarm set for 5 minutes later.", Toast.LENGTH_SHORT).show()
        timerViewModel.resetTimer(delay.toLong())

    }
    fun startAlarm_10(view: View?) {
        val delay: Int = 10 * 60 * 1000 // 10 minutes in milliseconds
        val alarm_time: Long = System.currentTimeMillis() + delay
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm_time, pendingIntent)
        Toast.makeText(this, "Alarm set for 10 minutes later.", Toast.LENGTH_SHORT).show()
        timerViewModel.resetTimer(delay.toLong())

    }
    fun startAlarm_15(view: View?) {
        val delay: Int = 15 * 60 * 1000 // 10 minutes in milliseconds
        val alarm_time: Long = System.currentTimeMillis() + delay
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm_time.toLong(), pendingIntent)
        Toast.makeText(this, "Alarm set for 15 minutes later.", Toast.LENGTH_SHORT).show()
        timerViewModel.resetTimer(delay.toLong())

    }

    fun startAlarm_20(view: View?) {
        val delay: Int = 20 * 60 * 1000 // 10 minutes in milliseconds
        val alarm_time: Long = System.currentTimeMillis() + delay
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm_time.toLong(), pendingIntent)
        Toast.makeText(this, "Alarm set for 20 minutes later.", Toast.LENGTH_SHORT).show()
        timerViewModel.resetTimer(delay.toLong())

    }


}