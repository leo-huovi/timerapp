package com.example.timerapp

import AlarmService
import MyWorker
import TimerViewModel
import android.app.ActivityManager
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

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun stopAlarmService() {
        val stopIntent = Intent(this, AlarmService::class.java)
        stopService(stopIntent)
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


    private fun enqueueMyWorkerWithDelay(delay: Long) {
        // Cancel the previous CoroutineWorker if it exists
        if (myWorkerRequest != null) {
            WorkManager.getInstance(this).cancelWorkById(myWorkerRequest!!.id)
            myWorkerRequest = null
        }

        // Cancel the AlarmService if it is running
        if (isServiceRunning(AlarmService::class.java)) {
            stopAlarmService()
        }

        // Create constraints (if needed)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        // Pass the delay as an input data for MyWorker
        val inputData = Data.Builder()
            .putLong("delay", delay)
            .build()

        // Create the one-time work request
        val workRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(inputData) // Pass the input data with delay
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, delay * 2, TimeUnit.MILLISECONDS)
            .build()

        // Enqueue the work and save the request for later use
        WorkManager.getInstance(this).enqueue(workRequest)
        myWorkerRequest = workRequest
    }

    fun startAlarm_5(view: View?) {
        val delay: Long = (5 * 60 * 1000).toLong() // 5 minutes in milliseconds
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
        val delay: Long = 15 * 60 * 1000 // 10 minutes in milliseconds
        enqueueMyWorkerWithDelay(delay)
        timerViewModel.resetTimer(delay)
        Toast.makeText(this, "Alarm set for 15 minutes later.", Toast.LENGTH_SHORT).show()
    }
    fun startAlarm_20(view: View?) {
        val delay: Long = 20 * 60 * 1000 // 10 minutes in milliseconds
        enqueueMyWorkerWithDelay(delay)
        timerViewModel.resetTimer(delay)
        Toast.makeText(this, "Alarm set for 20 minutes later.", Toast.LENGTH_SHORT).show()
    }

}