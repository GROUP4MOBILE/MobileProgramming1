package com.example.todoproject

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskName = intent.getStringExtra("taskName")
        val iconResId = intent.getIntExtra("iconResId", R.drawable.notifications) // Get icon resource ID

        // Create and show notification
        createNotification(context, taskName ?: "", iconResId)
    }

    @SuppressLint("MissingPermission")
    fun createNotification(context: Context, taskName: String, iconResId: Int) {
        val channelId = "reminder_channel"
        val notificationId = 1

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(iconResId) // Set notification icon
            .setContentTitle("Task Reminder")
            .setContentText("Task '$taskName' is due!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)

        // Create the Notification Channel (only for API 26 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Task Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        notificationManager.notify(notificationId, builder.build())
    }
}
