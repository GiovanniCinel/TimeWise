package com.example.timewise.notification

// Packages required by NotificationService class
import android.app.Service
import android.content.Intent
import android.os.IBinder

// This class is used to show a notification when the app is in the background
// It is started by the MyNotificationReceiver class when a notification is received
class NotificationService : Service() {

    // This method is called when the service is started using the onStartCommand() function
    // It handles the incoming intent, extracts the title, message, and notification ID from it,
    // and shows a notification using the NotificationUtils class if all the required data is available
    //
    // intent: The intent passed to the service.
    // flags: Additional data about the start request.
    // startId: A unique integer representing the start request.
    //
    // The return value specifies what the system should do if the service is terminated unexpectedly
    // In this case, it returns START_STICKY to indicate that the service should be restarted
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Extract the title, message, and notification ID from the intent
        val title = intent?.getStringExtra("title")
        val message = intent?.getStringExtra("message")
        val notificationId = intent?.getIntExtra("notificationId", -1)

        // Check if all the required data is available
        if (title != null && message != null && notificationId != null && notificationId != -1) {
            // Show a notification using the NotificationUtils class
            val notificationUtils = NotificationUtils(this)
            notificationUtils.showNotification(title, message, notificationId)
        }

        // Return START_STICKY to indicate that the service should be restarted if terminated unexpectedly
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
