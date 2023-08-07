import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.timerapp.R
import android.media.MediaPlayer
import android.os.Build
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.timerapp.ResetWorker
import java.util.concurrent.TimeUnit

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate() {
        super.onCreate()
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "app:alarmwakeclock"
        )

        // Initialize the media player here with the sound from R.raw.alarm
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Acquire the wake lock
        wakeLock.acquire()

        // Start playing the sound from the media player
        mediaPlayer?.start()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = createNotification()
            startForeground(1, notification)
        } else {
            showNotification()
        }

        // Enqueue the ResetWorker with a delay of 1 second
        val resetWorkRequest = OneTimeWorkRequest.Builder(ResetWorker::class.java)
            .setInitialDelay(1, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(this).enqueue(resetWorkRequest)

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "alarm_channel"
            val channelName = "Alarm Channel"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val channelId = "alarm_channel"
        val channelName = "Alarm Channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.alarm_10_minutes)
            .setContentTitle("Alarm")
            .setContentText("Time to wake up!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Remove the notification when tapped

        // You can also set the notification's sound and vibration here if desired

        return builder.build()
    }

    private fun stopAlarmService() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }

        if (wakeLock.isHeld) {
            wakeLock.release()
        }
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarmService()

        // Send a broadcast to indicate that the alarm has completed
        val alarmCompletedIntent = Intent("com.example.timerapp.ALARM_COMPLETED")
        sendBroadcast(alarmCompletedIntent)
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
            .setSmallIcon(R.drawable.alarm_10_minutes)
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
