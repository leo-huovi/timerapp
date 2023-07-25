// AlarmService.kt
package com.example.timerapp

// We can use wildcard imports for the classes from the same package, as well as reformat the import statements to group them based on their source package
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*

import com.example.timerapp.ui.theme.TImerAppTheme



//Activity:
// - private variables :  constants, properties (objects, integers, booleans)
// - private functions
// onStart,

// Service:
// - private variables :  constants, properties (objects, integers, booleans)
// - private functions
//  !!!!!!!!!!!!!!                  onCreate and onStartCommand are lifecycle callback methods for Android services, and  are automatically called by the Android system when the service is started. They are not meant to be called manually from the activity or any other component of the application.
// They are called automatically, when an intent with the service class's name is passed to start Service:
// val intent = Intent(this, MyService::class.java) ; startService(intent)
// !!!!!!!!!!!!!!!
//  onCreate(): This method is overridden from the base Service class. It is called when the service is created and used to perform initial setup. (onCreate does not receive any intent extras or data.) ( If the service was already running and gets started again, onCreate will not be called again, and the service will continue running in the same instance.)
// onStartCommand(): This method is also overridden from the base Service class. It is called when the service is started. (It handles the incoming start request and allows you to perform the desired functionality based on the intent data (extras) passed to the service.) (It itself does not "launch" the service. It is called to handle each start request, and it is executed by the Android system.)


class AlarmService : Service() {
    // A Service is a component in Android that runs in the background to perform long-running operations or handle tasks that don't require a user interface. It allows you to perform tasks that continue even if the app's UI is not visible or if the app is in the background or closed. ForegroundService, and yes, both AlarmManager and WorkManager are considered system services in Android too.

    // unique string identifier for a notification channel
    // Notification channels were introduced in Android 8.0 (API level 26) to give users more control over the types of notifications they receive from an app.
    private val NOTIFICATION_CHANNEL_ID = "TimerAppChannel"
    // In Android, layouts and resources can be accessed directly using R.layout and specific views within layouts using R.id. It is essential to note that this concept is different from Android's channel IDs, which are used for notification channels within the Android system."

    // Accessing layout file
    // setContentView(R.layout.activity_main);
    // Accessing view within layout
    // TextView textView = findViewById(R.id.textViewExample);


    private val NOTIFICATION_ID = 1
    // In summary, Channel ID is used to group notifications together for user control, while Notification ID is used to identify individual notifications and manage them in your code.
    // Channel ID is a concept introduced in Android 8.0 (API level 26) as part of the Notification Channels feature. It is used to group notifications into categories or channels, allowing users to have fine-grained control over the types of notifications they receive from your app.


    private val COUNTDOWN_TIME_SECONDS = 100
    //This constant is used in the startCountdown() function to initialize the remainingTime variable, which represents the countdown timer's duration in seconds. The countdown timer is implemented using a coroutine launched in a GlobalScope.launch block.
    // + The keyword private indicates that the property is only accessible within the scope of the class where it is declared, and val signifies that the property is read-only and cannot be reassigned after its initial value is set.


    // Without the lateinit modifier, the variable is no longer marked as being initialized later in the code, and it becomes a regular nullable property.  Nullable properties can hold null values, while non-nullable properties cannot.
    //  On the other hand, non-lateinit properties need to be initialized when they are declared or in the constructor.
    private lateinit var mediaPlayer: MediaPlayer
    // It is initialized in the onCreate() method of the Android Service. The onCreate() method is a lifecycle method of the Service class, and it is called when the service is created. This method is an appropriate place to initialize resources and set up the service.

    private var isRunning = false
    // The variable isRunning is accessed and used in the startCountdown() function of the code. It is used to control the execution of the countdown loop and stop the countdown when necessary.

    override fun onCreate() {
        super.onCreate()
        // While adding custom initialization logic is not inherently wrong, it should be done after calling super.onCreate() in the onCreate() method of an Activity.
        //    AppCompatActivity is the most commonly used activity class in modern Android development. It is a part of the Android Support Library (now part of AndroidX) and provides backward compatibility for newer features and UI components.
        //    It is suitable for apps targeting a wide range of Android versions and allows you to use the latest Material Design elements on older devices.
        //     Activity is the base class for all other activity classes in Android.
        //    It provides the core functionality for an activity but does not include some of the additional features and compatibility provided by AppCompatActivity.
        createNotificationChannel()

        // The this parameter represents the current context, typically referring to the Activity or Service that executes this code.
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //What did we learn in 20 minutes?
        //Intents can have string as extras, or they an have nothing (???) yet we pass them into services, on start
        // Are intents the variables we pass into a starting service, to customize it? NO, they are the addresses to servicse/acitivies

        //     Starting Activities: You can use an intent to launch another activity within your application or even in another application.
        //    Starting Services: Intents can be used to start and communicate with background services, allowing you to perform long-running tasks or operations even when the app is not actively running.
        //    Sending and Receiving Broadcasts: Intents can be used to send and receive broadcasts to/from different components in your app or even to other apps.

        // // Start the service using an explicit intent
        //        val intent = Intent(this, MyService::class.java) <--- intent is a parcel of an address of an intent
        //        startService(intent)
        // In summary, while activities can be launched without explicit intents if defined as the main entry point in the AndroidManifest.xml, services are typically started using explicit intents by specifying the target service class when calling startService().

        //  Intents can be thought of as parcels containing information about the target component (activity, service, broadcast receiver) and can carry additional data (like a parcel), but they don't necessarily represent the "address" of a service.
        if (intent?.action == "START_TIMER") {
            startForegroundService()
            // To start the Service, you can use startService(Intent) method. This method takes an Intent that identifies which Service to start. You can pass any necessary data to the Service using Intent extras.
            // Ensure you handle the Service's lifecycle appropriately. The Service's lifecycle can be controlled by calling startService(Intent) and stopService(Intent) or by the system when the Service is no longer needed.
            startCountdown()
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android O introduced notification channels
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "TimerApp Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundService() {
        //     A foreground service is a special type of Android Service that has a higher priority than regular background services.
        //     VS JobScheduler that reacts to conditions, WorkManager, that activates regularly
        //     Foreground services are used for long-running operations that are noticeable to the user and require ongoing interaction, such as playing music, handling incoming calls, or updating the user with important information.
        //    Foreground services show a persistent notification in the notification bar while they are running, which informs the user about the ongoing operation and helps prevent the Android system from killing the service due to low memory conditions.
        //    Foreground services are typically used to perform tasks that are critical to the user experience and require continuous execution, even when the app is in the background. They are bound to the lifecycle of the app, and if the app is closed or removed from the recent apps list, the foreground service will be terminated as well.
        val notificationIntent = Intent(this, MainActivity::class.java)
        // The first line creates an Intent targeting MainActivity, and the second line wraps that Intent into a PendingIntent for use in a notification."
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        // The PendingIntent.getActivity() method is used to obtain a PendingIntent object that represents the activity to be launched when the notification is clicked.
        // creating a simple PendingIntent for launching an activity, as shown in the code, is a common use case and relatively straightforward
        // the choice of request code (0 in this case) is important when working with multiple PendingIntent instances.

        // In all cases, the this keyword refers to the current instance of the AlarmService class, which is the immediate scope where the this keyword is used. It is used to provide the necessary context required for various operations, such as creating a MediaPlayer, building notifications, and creating explicit Intents to start activities.

        //Different Contexts in Asynchronous Tasks: In asynchronous tasks, such as callbacks, listeners, or background threads, the context might not be the same as the original context in which the code was executed. In these situations, using "this" explicitly ensures that you are referencing the intended context, especially if the task is executed within a different class or thread.
        //
        //Clear Intent for Context Usage: Explicitly providing the context using "this" can make the code more readable and self-documenting. It clarifies that the code is using the context of the current class, making it easier for other developers (and even for the original developer) to understand the code's intention.
        //
        //Callback Implementations: In callback implementations, "this" refers to the instance of the class implementing the callback interface. It allows the callback method to be executed on the correct object, ensuring that the appropriate context is used for handling the callback.
        //
        //Avoiding Implicit Dependencies on Outer Scope: Explicitly providing the context using "this" helps in avoiding implicit dependencies on the outer scope, which can lead to issues in more complex codebases.

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Timer is running")
            .setContentText("100 seconds remaining")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startCountdown() {
        isRunning = true
        // By having isRunning at the class level, you can control the coroutine from other parts of the code. For example, you might want to stop the countdown from a different method or in response to some user action
        // On the other hand, if you define a variable within a coroutine, it will only exist within the scope of that specific coroutine. Once the coroutine finishes execution, the variable will be deallocated, and you would lose track of its state

        // GlobalScope.launch is a function provided by Kotlin Coroutine's kotlinx.coroutines library. It is used to launch a new coroutine that runs in the background on the global scope. Coroutines are a way to perform asynchronous and non-blocking tasks in Android apps, and they are often used to handle tasks such as network requests, file I/O, or database operations without blocking the main thread.
        //  A coroutine is a lightweight thread-like mechanism that allows asynchronous and non-blocking programming.
        //When you use GlobalScope.launch, you are launching a coroutine that is not tied to any specific Android component's lifecycle, such as an Activity or Service. It runs independently of the main thread and can continue executing even if the app's UI is no longer visible.
        //Coroutines launched with GlobalScope.launch are typically used for background tasks that do not require any direct interaction with the UI, such as making network requests, performing database operations, or running computational tasks in the background.
        //It's important to note that coroutines launched with GlobalScope.launch may continue executing until completion, even if the app's process is killed or the user navigates away from the app.


        GlobalScope.launch {
            var remainingTime = COUNTDOWN_TIME_SECONDS
            while (isRunning && remainingTime > 0) {
                // The GlobalScope.launch function starts a coroutine that runs on a background thread. If there were no isRunning variable and the countdown loop kept running indefinitely, starting multiple countdowns could lead to multiple concurrent countdown processes.
                delay(1000)
                remainingTime--
                updateNotification(remainingTime)
            }
            isRunning = false
            mediaPlayer.start()
            stopForeground(true)
            stopSelf()
        }
    }

    private fun updateNotification(remainingTime: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Timer is running")
            .setContentText("$remainingTime seconds remaining")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}


// An Android Intent is an abstract description of an operation to be performed. It is used to communicate between components in an Android application and allows you to start activities, services, and broadcast receivers, as well as pass data between them.
// When used in the context of sending broadcasts, Intent is used to encapsulate the broadcast message, and BroadcastReceiver is used to receive and process that message.
// An Intent is a messaging object in Android that is used to request an action from another component (e.g., starting an activity, broadcasting a message, or starting a service).
//Intents are more versatile and can be used to start activities, services, or broadcast messages to other components in the system.
//Intents can also carry data using extras (similar to Bundles) to pass information between different components.
//Implicit Intents allow you to specify an action without specifying a specific target component, and the system will find the appropriate component to handle the action based on the intent's action and data.
//  The choice of using Bundle or Intent extras depends on your preference and the complexity of the data you need to pass. For simple data, using Intent extras directly is more concise, while Bundle is helpful when you need to pass multiple types of data or large amounts of data.

//            val intent = Intent(this, SecondActivity::class.java)  ( the target activity for the Intent.)
//            intent.putExtra("EXTRA_MESSAGE", "Hello from MainActivity using Intent Extra!")

// In summary, savedInstanceState is used to save and restore the state of an activity during configuration changes or when the system recreates the activity, while Intent is used to communicate between different components in your app and can carry data to be passed between these components. They have different purposes and are not interchangeable.



// WorkManager is a recommended solution for running background tasks, especially those that need to be executed even when the app is in the background or the device is in a doze or idle state. Unlike foreground services, WorkManager provides a more efficient and battery-friendly way to schedule and execute background tasks. It is suitable for deferrable tasks that do not require exact timing and can be optimized for battery life and system resources.  WorkManager uses various scheduling strategies, such as minimum latency, backoff, and constraints, to determine the best time to execute tasks based on factors like device charging status, network connectivity, and device idle state.
// AlarmManager  is suitable for tasks that require precise timing or need to be executed at specific times, such as setting reminders, notifications, or periodic data syncing. AlarmManager can schedule one-time or repeating alarms with different levels of precision, depending on the method used (set, setExact, setRepeating, etc.).