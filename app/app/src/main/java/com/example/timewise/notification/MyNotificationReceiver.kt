package com.example.timewise.notification

// Packages required by MyNotificationReceiver class
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

// This class is used to receive the notification data
class MyNotificationReceiver : BroadcastReceiver() {

    // Suppress added to show problems associated with AI generated code
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        // Extract the data from the received intent
        val title = intent?.getStringExtra("title")
        val message = intent?.getStringExtra("message")
        val notificationId = intent?.getIntExtra("notificationId", -1)

        // Check if the required data is present and valid
        if (title != null && message != null && notificationId != null && notificationId != -1) {
            // Create an instance of NotificationUtils with the provided context
            val notificationUtils = NotificationUtils(context!!)

            // Show the notification using the extracted data
            notificationUtils.showNotification(title, message, notificationId)
        }
    }
}
