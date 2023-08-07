import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.timerapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext



class MyWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val applicationContext = context.applicationContext


    override suspend fun doWork(): Result {
        val powerManager = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "myapp:alarmwakeclock"
        )
        // Acquire the lock
        wakeLock.acquire()

        // Create a notification channel for Foreground Service (required for API level 26 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "timer_channel"
            val channelName = "Timer Service Channel"
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW // Use LOW importance to prevent screen lighting up
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Release the lock after acquiring it so that the screen doesn't light up
        wakeLock.release()

        // Get the delay passed from MainActivity
        val delay = inputData.getLong("delay", 0)

        // Set the worker into foreground to display the progress
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val notification = createNotification()
        val foregroundInfo = ForegroundInfo(1, notification)
        setForeground(foregroundInfo)

        // Start the Foreground Service
        val serviceIntent = Intent(applicationContext, AlarmService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(applicationContext, serviceIntent)
        } else {
            applicationContext.startService(serviceIntent)
        }

        // Perform your alarm clock countdown logic here
        // Use a background context to run the countdown
        // Perform your alarm clock countdown logic here
        // Use a background context to run the countdown
        withContext(Dispatchers.Default) {
            // Example: Countdown based on the provided delay
            for (i in (delay / 1000) downTo 1) {
                delay(1000) // Simulate 1 second delay
                withContext(Dispatchers.Main) {
                    // Update the notification with the remaining time on the main thread
                    updateNotification(i)
                }
            }

            // When the countdown is complete, call the alarm service to stop the alarm
            stopAlarmService()

            // Show the final notification with sound on the main thread
            withContext(Dispatchers.Main) {
                showFinalNotification()
            }
        }

        // Work is done, return success
        return Result.success()
    }


    private fun createNotification(): Notification {
        val channelId = "alarm_channel"
        val channelName = "Alarm Channel"
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        // Create the notification channel (for API level 26 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW) // Use LOW importance to prevent screen lighting up
            notificationManager.createNotificationChannel(channel)
        }

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.alarm_10_minutes)
            .setContentTitle("Alarm")
            .setContentText("Time to wake up!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Remove the notification when tapped

        // You can also set the notification's vibration here if desired

        return builder.build()
    }

    private fun updateNotification(remainingTime: Long) {
        val channelId = "alarm_channel"
        val channelName = "Alarm Channel"
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        // Create the notification channel (for API level 26 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW) // Use LOW importance to prevent screen lighting up
            notificationManager.createNotificationChannel(channel)
        }

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.alarm_10_minutes)
            .setContentTitle("Alarm")
            .setContentText("Time to wake up in $remainingTime seconds!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Remove the notification when tapped

        notificationManager.notify(1, builder.build())
    }

    private fun showFinalNotification() {
        val channelId = "final_notification_channel"
        val channelName = "Final Notification Channel"
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        // Create the notification channel for the final notification (for API level 26 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH) // Use HIGH importance for final notification with sound
            notificationManager.createNotificationChannel(channel)
        }

        val soundUri = Uri.parse("android.resource://com.example.timerapp/${R.raw.alarm}") // Replace "your_final_sound_file" with the file name of your final sound in the raw folder

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.alarm_10_minutes)
            .setContentTitle("Time's up!")
            .setContentText("Alarm finished.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Remove the notification when tapped
            .setSound(soundUri) // Set custom sound for the final notification here

        notificationManager.notify(2, builder.build())
    }

    private fun stopAlarmService() {
        // Stop the alarm service (AlarmService)
        val stopIntent = Intent(applicationContext, AlarmService::class.java)
        applicationContext.stopService(stopIntent)

        // Cancel the notification that was used for the alarm countdown
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.cancel(1)
    }
}