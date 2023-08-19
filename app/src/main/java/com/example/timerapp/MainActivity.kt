package com.example.timerapp

import TimerViewModel
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.timerapp.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import com.jakewharton.threetenabp.AndroidThreeTen


class MainActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null
    private var alarmPlayer: MediaPlayer? = null
    private var hatched: Boolean = false
    private var delay: Int = 0
    private val date_format = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private lateinit var binding: ActivityMainBinding
    private lateinit var timerViewModel: TimerViewModel

    private lateinit var pokemon_image_today: String
    private lateinit var pokemon_of_today: String
    private lateinit var nature_of_today: String
    private lateinit var gender_of_today: String
    private var today_exp: Int = 0
    private var max_count: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidThreeTen.init(this)





        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

        sharedPref = this.getSharedPreferences("mysharedpref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        //Pokemon logic



        //        val editor = sharedPref.edit()
        //          editor.putInt("max_count", 0)
//            editor.apply()

        val today = LocalDate.now().format(date_format)
        val yesterday = sharedPref.getString("last_date", LocalDate.of(2000, 1, 1).format(date_format))

        if (yesterday != today) {


            if (hatched) {
                var last_pokemon: String = yesterday + "," + sharedPref.getInt("max_count", 0)
                    .toString() + "," + sharedPref.getString("pokemon_image_of_day", "egg")
                    .toString() + "," + sharedPref.getString("pokemon_of_day", "Egg")
                    .toString() + "," + sharedPref.getString("nature_of_day", "Neutral")
                    .toString() + "," + sharedPref.getString("pokemon_gender_of_day", "⚥")
                    .toString()
                editor.putString(yesterday, last_pokemon)
                editor.apply()

                hatched = false

            } else {

                var last_pokemon : String = yesterday + "," + sharedPref.getInt("max_count", 0)
                    .toString() + "," + "egg" + "," + "Egg" + "," + sharedPref.getString("nature_of_day", "Neutral")
                    .toString() + "," + sharedPref.getString("pokemon_gender_of_day", "⚥")
                    .toString()
                editor.putString(yesterday, last_pokemon)
                editor.apply()

                hatched = false
            }



            val exp_now = Random.nextInt(40,60)

            val pokemon_names_all = getString(R.string.all_pokemon_names)
            var values = pokemon_names_all.split(",").map { it.trim() }

            // val pokemonIdNumber = (1..values.size).map { it.toString() }.toTypedArray()
            var randomIndex = Random.nextInt(0, values.size)

            val pokemon_chosen_now = values[randomIndex]
            val pokemon_image_now = "pokemon" + randomIndex

            val nature_names_all = getString(R.string.all_natures)
            values = nature_names_all.split(",").map { it.trim() }
            randomIndex = Random.nextInt(0, values.size)
            val nature_chosen_now = values[randomIndex]
            val gender_now = listOf("♂","♀")[Random.nextInt(0,2)]

            editor.putString("nature_of_day", nature_chosen_now)
            editor.apply()
            editor.putString("pokemon_of_day", pokemon_chosen_now)
            editor.apply()
            editor.putString("pokemon_image_of_day", pokemon_image_now)
            editor.apply()
            editor.putString("pokemon_gender_of_day", gender_now)
            editor.apply()
            editor.putInt("today_exp", exp_now)
            editor.apply()


            editor.putString("last_date", today)
            editor.apply()
            editor.putInt("max_count", 0)
            editor.apply()


            pokemon_of_today = "Egg"
            binding.textView2.text = pokemon_of_today
            pokemon_image_today = "egg"

            max_count = sharedPref.getInt("max_count", 0)
            today_exp = sharedPref.getInt("today_exp", 10)

        } else {


            max_count = sharedPref.getInt("max_count", 0)
            today_exp = sharedPref.getInt("today_exp", 10)
            binding.progressBar.max = today_exp

            if (max_count >= today_exp) {
                hatched = true
                pokemon_of_today = sharedPref.getString("pokemon_of_day", "Egg").toString()
                nature_of_today = sharedPref.getString("nature_of_day", "Neutral").toString()
                gender_of_today = sharedPref.getString("pokemon_gender_of_day", "⚥").toString()

                binding.textView2.text = pokemon_of_today + "\n(" + gender_of_today + ") "  + nature_of_today
                pokemon_image_today = sharedPref.getString("pokemon_image_of_day", "egg").toString()



            } else {
                pokemon_of_today = "Egg"
                binding.textView2.text = pokemon_of_today
                pokemon_image_today = "egg"
            }



        }


        var resourceId = resources.getIdentifier(pokemon_image_today, "drawable", packageName)
        var imageView = findViewById<ImageView>(R.id.pokemon)
        imageView.setImageResource(resourceId)

        if (max_count >= today_exp) {
            binding.progressBar.visibility = View.INVISIBLE
            binding.textView3.text = "Level " + (max_count - today_exp + 5).toString()
        } else {
            binding.progressBar.max = today_exp
            binding.progressBar.setProgress(max_count)
            binding.textView3.text = "Hatches in:\n" + max_count.toString() + " / " + today_exp.toString()
        }

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
        var editor = sharedPref.edit()

        var remaining_seconds = (timeInMillis / 1000).toInt()

        if (remaining_seconds == 0) {

            var score = delay / 5

            var new_max_count = sharedPref.getInt("max_count", 0) + score
            editor.putInt("max_count", new_max_count)
            editor.apply()
            max_count = sharedPref.getInt("max_count", 0)

            if (max_count >= today_exp) {
                hatched = true

                var today_exp = sharedPref.getInt("today_exp", 10)
                binding.progressBar.max = today_exp
                val pokemon_of_today = sharedPref.getString("pokemon_of_day", "Egg")
                val nature_of_today = sharedPref.getString("nature_of_day", "Neutral")
                gender_of_today = sharedPref.getString("pokemon_gender_of_day", "⚥").toString()
                binding.progressBar.visibility = View.INVISIBLE
                binding.textView2.text = pokemon_of_today + "\n(" + gender_of_today + ") "  + nature_of_today
                binding.textView3.text = "Level " + (max_count - today_exp + 5).toString()
                val pokemon_image_today = sharedPref.getString("pokemon_image_of_day", "egg")
                val resourceId = resources.getIdentifier(pokemon_image_today, "drawable", packageName)
                val imageView = findViewById<ImageView>(R.id.pokemon)
                imageView.setImageResource(resourceId)


            } else {
                max_count = sharedPref.getInt("max_count", 0)
                val pokemon_of_today = "Egg"
                binding.progressBar.setProgress(max_count)
                binding.textView2.text = pokemon_of_today
                binding.textView3.text = "Hatches in: " + max_count.toString() + " / " + today_exp.toString()


            }
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