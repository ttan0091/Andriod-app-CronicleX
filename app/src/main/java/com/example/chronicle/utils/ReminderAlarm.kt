package com.example.chronicle.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.chronicle.R

/**
 * ReminderAlarm is used to handle reminder alarms and display notifications
 * **/
@RequiresApi(Build.VERSION_CODES.O)
class ReminderAlarm : BroadcastReceiver() {
    /**
     * if there's incoming broadcast intents, retrieve stored data from sharedPreference
     * and then displaying the data in the notification
     * **/
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if (context != null && intent != null) {
                val sharedPreferences =
                    context.getSharedPreferences("AlarmData", Context.MODE_PRIVATE)
                val title = sharedPreferences.getString("title", null)
                val message = sharedPreferences.getString("message", null)
                if (title != null && message != null) {
                    showNotification(context, title, message)
                }
            }
        } catch (e: Exception) {
            Log.e("reminder", e.toString())
        }
    }

    /**
     * method to create and show the notification on specified channel
     *
     * context: current context
     * title: notification title
     * desc: notification description
     * **/
    private fun showNotification(context: Context, title: String, desc: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminder_channel"
        val channelName = "reminder_channel_name"
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(desc)
            .setSmallIcon(R.mipmap.ic_launcher_round)

        manager.notify(1, builder.build())
    }
}

