    package com.example.timerapp

    import TimerViewModel
    import android.Manifest
    import android.accessibilityservice.AccessibilityService
    import android.app.AlarmManager
    import android.app.PendingIntent
    import android.content.Context
    import android.content.Intent
    import android.content.SharedPreferences
    import android.content.pm.PackageManager
    import android.media.MediaPlayer
    import android.os.Bundle
    import android.provider.Settings
    import android.text.TextUtils.SimpleStringSplitter
    import android.util.Log
    import android.view.View
    import android.view.accessibility.AccessibilityEvent
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import androidx.lifecycle.Observer
    import androidx.lifecycle.ViewModelProvider
    import com.example.timerapp.databinding.ActivityMainBinding
    import java.text.SimpleDateFormat
    import java.util.Calendar
    import java.util.Date
    import java.util.Locale




    class MainActivity : AppCompatActivity() {
        private lateinit var sharedPref: SharedPreferences
        private var alarmManager: AlarmManager? = null
        private var pendingIntent: PendingIntent? = null
        private var alarmPlayer: MediaPlayer? = null
        private var delay: Int = 0

        private lateinit var binding: ActivityMainBinding
        private lateinit var timerViewModel: TimerViewModel



        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)


            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

            sharedPref = this.getSharedPreferences("mysharedpref", Context.MODE_PRIVATE)

            val max_count = sharedPref.getInt("max_count", 0)
            binding.progressBar.setProgress( max_count % 10 )
            binding.textView2.text = max_count.toString()


            // Observe the timer LiveData and update the TextView
            timerViewModel.currentTime.observe(this, Observer { timeInMillis ->
                updateTimerText(timeInMillis)
                updateUI(timeInMillis)


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


        fun updateUI(timeInMillis: Long) {
            // Update the UI with the counter values
            // var current_count = findViewById<TextView>(R.id.textView_2)
            // current_count.text = max_count.toString()

            var remaining_seconds = timeInMillis % 1000

            if (remaining_seconds == 1L) {

                var score = delay % 5

                val max_count = sharedPref.getInt("max_count", 0) + score
                val editor = sharedPref.edit()
                editor.putInt("max_count", max_count)
                editor.apply()
                binding.textView2.text = max_count.toString()
                binding.progressBar.setProgress( max_count % 10 )
            }
        }



        private fun updateTimerText(timeInMillis: Long) {
            val format = SimpleDateFormat("mm:ss", Locale.getDefault())
            val formattedTime = format.format(Date(timeInMillis))
            binding.textView.text = formattedTime
        }

        fun playAlarmIn5Minutes(view: View?) {
            delay = 5
            scheduleAlarm(delay)
            timerViewModel.resetTimer(delay * 60 * 1000L)
        }

        fun playAlarmIn10Minutes(view: View?) {
            delay = 10
            scheduleAlarm(delay)
            timerViewModel.resetTimer(delay * 60 * 1000L)
        }

        fun playAlarmIn15Minutes(view: View?) {
            delay = 15
            scheduleAlarm(delay)
            timerViewModel.resetTimer(delay * 60 * 1000L)
        }

        fun playAlarmIn20Minutes(view: View?) {
            delay = 20
            scheduleAlarm(delay)
            timerViewModel.resetTimer(delay * 60 * 1000L)
        }

        private fun scheduleAlarm(minutes: Int) {
            // Cancel any previous alarm
            cancelAlarm()


            // Calculate the alarm time in milliseconds
            val timeInMillis = System.currentTimeMillis() + minutes * 60 * 1000

            // Create an instance of Calendar and set the desired alarm time
            val alarmTime = Calendar.getInstance()
            alarmTime.timeInMillis = timeInMillis

            // Create an intent to launch the AlarmReceiver
            val intent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

            // Create an instance of AlarmClockInfo with the desired alarm time and PendingIntent
            val alarmClockInfo = AlarmManager.AlarmClockInfo(alarmTime.timeInMillis, pendingIntent)

            // Set the alarm using setAlarmClock
            alarmManager?.setAlarmClock(alarmClockInfo, pendingIntent)
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