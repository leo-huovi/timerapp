package com.example.timerapp

import TimerViewModel
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import com.example.timerapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

import androidx.work.*

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


        if (savedInstanceState != null) {
            timerViewModel.restoreInstanceState(savedInstanceState)
        } else {
            timerViewModel.startTimer(0)
        }

    }
        // Set a click listener on a button to start the timer with a specified time

    private var myWorkerRequest: WorkRequest? = null

    private fun enqueueMyWorkerWithDelay(delay: Long) {
        // Cancel the previous work request if it exists
        myWorkerRequest?.let {
            WorkManager.getInstance(this).cancelWorkById(it.id)
        }

        // Create constraints (if needed)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        // Create the one-time work request
        val workRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        // Enqueue the work and save the request for later use
        WorkManager.getInstance(this).enqueue(workRequest)
        myWorkerRequest = workRequest
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        timerViewModel.saveInstanceState(outState)
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContentView(R.layout.activity_main)
    }

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
        val delay: Long = 5 * 60 * 1000 // 10 minutes in milliseconds
        enqueueMyWorkerWithDelay(delay)
        timerViewModel.resetTimer(delay)
        Toast.makeText(this, "Alarm set for 5 minutes later.", Toast.LENGTH_SHORT).show()
    }

    fun startAlarm_10(view: View?) {
        val delay: Long = 10 * 60 * 1000 // 10 minutes in milliseconds
        enqueueMyWorkerWithDelay(delay)
        timerViewModel.resetTimer(delay)
        Toast.makeText(this, "Alarm set for 10 minutes later.", Toast.LENGTH_SHORT).show()
    }

    fun startAlarm_15(view: View?) {
        val delay: Long = 15 * 60 * 1000 // 15 minutes in milliseconds
        enqueueMyWorkerWithDelay(delay)
        timerViewModel.resetTimer(delay)
        Toast.makeText(this, "Alarm set for 15 minutes later.", Toast.LENGTH_SHORT).show()
    }

    fun startAlarm_20(view: View?) {
        val delay: Long = 20 * 60 * 1000 // 20 minutes in milliseconds
        enqueueMyWorkerWithDelay(delay)
        timerViewModel.resetTimer(delay)
        Toast.makeText(this, "Alarm set for 20 minutes later.", Toast.LENGTH_SHORT).show()
    }


}