package com.example.silencer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.getSystemService

class AlarmReceiver2 : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        var nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !nm.isNotificationPolicyAccessGranted){
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
        createNotificationChannel(context)
        sendNotification(context)
        var am : AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.ringerMode = AudioManager.RINGER_MODE_NORMAL

    }

    private fun sendNotification(context: Context) {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, "channel0")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Device Silencer")
            .setContentText("Your device is set to normal mode.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(100, builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notif channel"
            val desc = "Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("channel1", name, importance)
            channel.description = desc
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}