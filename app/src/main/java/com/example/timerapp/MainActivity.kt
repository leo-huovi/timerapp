    package com.example.timerapp

    import TimerViewModel
    import androidx.lifecycle.ViewModelProvider
    import android.app.AlarmManager
    import android.app.PendingIntent
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.media.MediaPlayer
    import android.os.Bundle
    import android.view.View
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import android.Manifest
    import androidx.lifecycle.Observer
    import com.example.timerapp.AlarmReceiver
    import com.example.timerapp.databinding.ActivityMainBinding
    import java.text.SimpleDateFormat
    import java.util.Date
    import java.util.Locale

    class MainActivity : AppCompatActivity() {
        private var alarmManager: AlarmManager? = null
        private var pendingIntent: PendingIntent? = null
        private var alarmPlayer: MediaPlayer? = null

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

            requestAlarmPermission()

            // Initialize the AlarmManager
            alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        }


        private fun updateTimerText(timeInMillis: Long) {
            val format = SimpleDateFormat("mm:ss", Locale.getDefault())
            val formattedTime = format.format(Date(timeInMillis))
            binding.textView.text = formattedTime
        }

        fun playAlarmIn5Minutes(view: View?) {
            var delay = 5
            scheduleAlarm(delay)
            timerViewModel.resetTimer(delay * 60 * 1000L)
        }

        fun playAlarmIn10Minutes(view: View?) {
            var delay = 10
            scheduleAlarm(delay)
            timerViewModel.resetTimer(delay * 60 * 1000L)
        }

        fun playAlarmIn15Minutes(view: View?) {
            var delay = 15
            scheduleAlarm(delay)
            timerViewModel.resetTimer(delay * 60 * 1000L)
        }

        fun playAlarmIn20Minutes(view: View?) {
            var delay = 20
            scheduleAlarm(delay)
            timerViewModel.resetTimer(delay * 60 * 1000L)
        }

        private fun scheduleAlarm(seconds: Int) {
            // Cancel any previous alarm
            cancelAlarm()

            // Calculate the alarm time in milliseconds
            val timeInMillis = System.currentTimeMillis() +  seconds * 60 * 1000

            // Create an intent to launch the AlarmReceiver
            val intent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

            // Schedule the alarm using AlarmManager
            alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
        private fun cancelAlarm() {
            // Cancel any previous alarm
            if (alarmManager != null && pendingIntent != null) {
                alarmManager!!.cancel(pendingIntent!!)
                pendingIntent!!.cancel()
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            // Cancel any scheduled alarms and release MediaPlayer resources
            cancelAlarm()
            stopAlarm()

        }

        private fun stopAlarm() {
            // Stop and release the MediaPlayer resources
            if (alarmPlayer != null) {
                alarmPlayer!!.stop()
                alarmPlayer!!.release()
                alarmPlayer = null
            }
        }

        private val PERMISSION_REQUEST_CODE = 1

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            timerViewModel.saveInstanceState(outState)
        }

        override fun onRestoreInstanceState(savedInstanceState: Bundle) {
            super.onRestoreInstanceState(savedInstanceState)
            timerViewModel.restoreInstanceState(savedInstanceState)

        }

        private fun requestAlarmPermission() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(Manifest.permission.SET_ALARM),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }