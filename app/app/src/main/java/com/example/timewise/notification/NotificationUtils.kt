package com.example.timewise.notification

// Packages required by NotificationUtils class
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.timewise.MainActivity
import com.example.timewise.R
import java.util.*

// NotificationUtils class manages app notifications
class NotificationUtils(private val context: Context) {
    // Notification channel ID
    private val channelId = "my_channel_id"

    // Suppress added because minSdkVersion is higher
    @SuppressLint("ObsoleteSdkInt", "LaunchActivityFromNotification")
    // Show a notification with the given title, message, and notification ID
    fun showNotification(title: String, message: String, notificationId: Int) {
        // Create an intent to start the NotificationService (custom service)
        val notificationIntent = Intent(context, NotificationService::class.java)
        notificationIntent.putExtra("title", title)
        notificationIntent.putExtra("message", message)
        notificationIntent.putExtra("notificationId", notificationId)

        // Log the notification details
        Log.d(TAG, "$title, $message, $notificationIntent")

        // Generate a random request code for the pending intent
        val requestCode = Random().nextInt()

        // Create a pending intent to start the NotificationService
        val pendingIntent = PendingIntent.getService(
            context,
            requestCode,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification using NotificationCompat.Builder
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Get the system's NotificationManager service
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Notify the notificationManager to show the notification with the given notification ID
        notificationManager.notify(notificationId, notificationBuilder.build())

        // Create a second notification with a different PendingIntent (to MainActivity)
        // This ensures that pressing on the notification will open the app
        val notificationIntent2 = Intent(context, MainActivity::class.java)
        notificationIntent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notificationIntent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent2 = PendingIntent.getActivity(
            context,
            requestCode,
            notificationIntent2,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationBuilder2 = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent2)
        val notificationManager2 =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager2.createNotificationChannel(channel)
        }

        // Notify the notificationManager2 to show the second notification with the given notification ID
        notificationManager2.notify(notificationId, notificationBuilder2.build())
    }

    // Schedule a notification to be shown at the given date with the given title, message, and notification ID
    fun scheduleNotification(date: Date, title: String, message: String, notificationId: Int) {
        // Get an instance of the Calendar class
        val calendar = Calendar.getInstance()
        calendar.time = date

        // Get an instance of the AlarmManager class
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create an intent for the broadcast receiver
        val intent = Intent(context, MyNotificationReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("message", message)
        intent.putExtra("notificationId", notificationId)

        // Generate a random request code for the pending intent
        val requestCode = Random().nextInt()

        // Create a pending intent for the broadcast receiver
        val pendingIntent =
            PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)

        // Schedule the alarm to trigger at the specified date and time
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        savePendingIntentRequestCode(context, notificationId, requestCode)
    }

    // Save the pending intent request code of the given notification
    private fun savePendingIntentRequestCode(
        context: Context,
        notificationId: Int,
        requestCode: Int
    ) {
        // Save the requestCode associated with the notification ID
        val sharedPreferences =
            context.getSharedPreferences("NotificationRequestCodes", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("RequestCode_$notificationId", requestCode)
        editor.apply()
    }

    // Cancel a notification of the given notification
    fun cancelNotification(context: Context, notificationId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Retrieve the requestCode associated with the notification ID
        val requestCode = getPendingIntentRequestCode(context, notificationId)

        // Check if the requestCode is valid
        if (requestCode != -1) {
            Log.d(
                TAG,
                "cancelNotification: notificationId = $notificationId, requestCode = $requestCode"
            )
            // Create an intent for the alarm
            val intent = Intent(context, MyNotificationReceiver::class.java)
            intent.putExtra("notificationId", notificationId)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            )

            // Cancel the alarm associated with the requestCode
            alarmManager.cancel(pendingIntent)
        } else {
            Log.d(TAG, "cancelNotification: requestCode = $requestCode")
        }

        // Cancel the notification display using NotificationManager
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(notificationId)
    }

    // Get the pending intent request code of the given notification
    private fun getPendingIntentRequestCode(context: Context, notificationId: Int): Int {
        // Retrieve the requestCode associated with the notification ID
        val sharedPreferences =
            context.getSharedPreferences("NotificationRequestCodes", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("RequestCode_$notificationId", -1)
    }
}