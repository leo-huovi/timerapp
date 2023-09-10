
# An android kotlin timer app ⏰

TimerApp is an Android application that allows users to set timers and play alarms after a specified 
duration. It also features a countdown timer that updates in real-time and allows users to play alarms at 
different intervals.

This app was created as a practice project to learn how Android app development works. The motivation behind 
creating this app was my own need for a tool that helps me stay focused and manage my time effectively.

## Features

- Set a timer for 5, 10, 15, or 20 minutes.
- Play an alarm after the specified duration.
- Countdown timer that updates in real-time.
- Change the theme of the app.
- Save the maximum count reached.
- Display a list of past days with their respective data.

## The tricky AlarmManager class
The TimerApp utilizes the AlarmManager and AlarmReceiver to schedule and play alarms. Here's how it works:

1. When the user sets a timer duration and starts the countdown, the app calculates the alarm time in 
milliseconds.
2. It creates an instance of Calendar and sets the desired alarm time.
3. An intent is created to launch the AlarmReceiver class, which handles the alarm functionality.
4. A PendingIntent is created with the intent and used to schedule the alarm using the AlarmManager.
5. The AlarmClockInfo is created with the desired alarm time and PendingIntent, and it is set using the 
setAlarmClock method of the AlarmManager.
6. When the alarm time is reached, the AlarmReceiver class receives the broadcast and plays the alarm sound 
using a MediaPlayer.
7. The alarm is stopped and released when the app is destroyed.
