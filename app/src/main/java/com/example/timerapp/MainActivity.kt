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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.timerapp.databinding.ActivityMainBinding
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random


class MainActivity : BaseActivity() {
    private var imageAdapter: ImageAdapter? = null
    private lateinit var sharedPref: SharedPreferences
    private var alarmManager: AlarmManager? = null
    private var pendingIntent: PendingIntent? = null
    private var alarmPlayer: MediaPlayer? = null
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

        AndroidThreeTen.init(this)


        val themes = arrayOf(
            R.style.Theme_BlueTimer,
            R.style.Theme_PinkTimer,
            R.style.Theme_OrangeTimer,
            R.style.Theme_BlackTimer,
            R.style.Theme_GreenTimer,
            R.style.Theme_WhiteTimer,
            R.style.Theme_PurpleTimer,
            R.style.Theme_RedTimer
        )

        sharedPref = this.getSharedPreferences("mysharedpref", Context.MODE_PRIVATE)

        fun setCurrentTheme(current_theme: Int) {
            setTheme(themes[current_theme])
        }



        fun switchTheme() {
            var currentThemeIndex = sharedPref.getInt("themeIndex", 0)
            currentThemeIndex = (currentThemeIndex + 1) % themes.size
            sharedPref.edit().putInt("themeIndex", currentThemeIndex).commit()
            setCurrentTheme(currentThemeIndex)
        }



        val editor = sharedPref.edit()

        //Pokemon logic



        //        val editor = sharedPref.edit()
        //          editor.putInt("max_count", 0)
//            editor.apply()

        // Manually change past days if update failed:
        // editor.putString("2023-08-26", "2023-08-26,30,pokemon472,Gliscor,Hasty,♀")
        // editor.commit()


        val today = LocalDate.now().format(date_format)
        val yesterday = sharedPref.getString("last_date", LocalDate.of(2000, 1, 1).format(date_format))


        var hatched = sharedPref.getBoolean("hatched", false)

        var currentThemeIndex = sharedPref.getInt("themeIndex", 0)
        setCurrentTheme(currentThemeIndex)

        if (yesterday != today) {
            switchTheme()

            if (hatched) {

                var last_pokemon: String = yesterday + "," + sharedPref.getInt("max_count", 0)
                    .toString() + "," + sharedPref.getString("pokemon_image_of_day", "egg")
                    .toString() + "," + sharedPref.getString("pokemon_of_day", "Egg")
                    .toString() + "," + sharedPref.getString("nature_of_day", "Neutral")
                    .toString() + "," + sharedPref.getString("pokemon_gender_of_day", "⚥")
                    .toString()
                editor.putBoolean("hatched", false)
                editor.commit()
                editor.putString(yesterday, last_pokemon)
                // editor.apply()
                editor.commit()
                val exp_now = Random.nextInt(20,30)
                editor.putInt("today_exp", exp_now)
                editor.commit()


            } else {

                var last_pokemon : String = yesterday + "," + sharedPref.getInt("max_count", 0)
                    .toString() + "," + "egg" + "," + "Egg" + "," + sharedPref.getString("nature_of_day", "Neutral")
                    .toString() + "," + sharedPref.getString("pokemon_gender_of_day", "⚥")
                    .toString()
                editor.putString(yesterday, last_pokemon)
                // editor.apply()
                editor.commit()
                val exp_now = Random.nextInt(15,25)
                editor.putInt("today_exp", exp_now)
                editor.commit()

            }


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
            // editor.apply()
            editor.commit()
            editor.putString("pokemon_of_day", pokemon_chosen_now)
            // editor.apply()
            editor.commit()
            editor.putString("pokemon_image_of_day", pokemon_image_now)
            // editor.apply()
            editor.commit()
            editor.putString("pokemon_gender_of_day", gender_now)
            // editor.apply()
            editor.commit()



            editor.putString("last_date", today)
            // editor.apply()
            editor.commit()
            editor.putInt("max_count", 0)
            // editor.apply()
            editor.commit()


            pokemon_of_today = "Egg"
            pokemon_image_today = "egg"

            max_count = sharedPref.getInt("max_count", 0)
            today_exp = sharedPref.getInt("today_exp", 10)

        } else {


            max_count = sharedPref.getInt("max_count", 0)
            today_exp = sharedPref.getInt("today_exp", 10)

            if (max_count >= today_exp) {
                editor.putBoolean("hatched", true)
                editor.commit()
                pokemon_of_today = sharedPref.getString("pokemon_of_day", "Egg").toString()
                nature_of_today = sharedPref.getString("nature_of_day", "Neutral").toString()
                gender_of_today = sharedPref.getString("pokemon_gender_of_day", "⚥").toString()

                // binding.textView2.text = pokemon_of_today + "\n(" + gender_of_today + ") "  + nature_of_today
                pokemon_image_today = sharedPref.getString("pokemon_image_of_day", "egg").toString()



            } else {
                pokemon_of_today = "Egg"
                // binding.textView2.text = pokemon_of_today
                pokemon_image_today = "egg"
            }




        }


        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)


        binding.textView2.text = pokemon_of_today


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


        // Initialize the RecyclerView
        var recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)

        // Set the layout manager and span count for GridLayout
        // recyclerView.layoutManager = GridLayoutManager(this, 2)


        val startDate = LocalDate.now().minusDays(20)
        val endDate = LocalDate.now()
        //val endDate = LocalDate.now().minusDays(1)
        val date_format = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val images = mutableListOf<Int>()
        val levels = mutableListOf<String>()

        var currentDate = endDate
        while (!currentDate.isBefore(startDate)) {

            //Flush the old history:
            //editor.putString(currentDate.format(date_format).toString(), "day,50,empty,Egg,Neutral,Gender")
            // editor.commit()
            val pokemon_level: String = sharedPref.getString(
                currentDate.format(date_format).toString(),
                "day,0,pokemon0,Egg,Neutral,Gender"
            )?.split(",")?.get(1) ?: ""

            val pokemon_image_on_grid: String = sharedPref.getString(currentDate.format(date_format).toString(), "day,50,pokemon0,Egg,Neutral,Gender")?.split(",")?.get(2) ?: ""
            val resourceId = resources.getIdentifier(pokemon_image_on_grid, "drawable", packageName)
            images.add(resourceId)
            levels.add(pokemon_level)
            currentDate = currentDate.minusDays(1)
        }


        val imageAdapter = ImageAdapter(images, levels)
        recyclerView.adapter = imageAdapter


        // binding.textView.text = sharedPref.getString("max_count", 0)
    }


    // Call this function to update the images
    private fun updateImages(newImages: List<Int>, newLevels: List<String>) {
        // Update the images in the ImageAdapter
        imageAdapter?.images = newImages
        imageAdapter?.texts = newLevels

        // Notify the adapter that the data has changed
        imageAdapter?.notifyDataSetChanged()
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
            // editor.apply()
            editor.commit()
            max_count = sharedPref.getInt("max_count", 0)




            if (max_count >= today_exp) {
                editor.putBoolean("hatched", true)
                editor.commit()

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


            // binding.textView.text = LocalDate.now().format(date_format).toString()
            // binding.textView.text = sharedPref.getString("2023-08-16", "Egg").toString()?.split(",")?.get(2)

            //DEBUG    Check data of past days:
            // binding.textView.textSize = 7.toFloat()
            // binding.textView.text = "   " + sharedPref.getString("2023-08-24", "egg")+ "   " + sharedPref.getString("2023-08-25", "egg")            + "   " + sharedPref.getString("2023-08-26", "egg")
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
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

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

