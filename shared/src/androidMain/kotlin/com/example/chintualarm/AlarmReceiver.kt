package com.example.chintualarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Ensure AndroidContext is initialized even if app is in background/killed
        AndroidContext.applicationContext = context.applicationContext
        
        val action = intent.action
        val alarmId = intent.getStringExtra("ALARM_ID")
        
        if (action == "ACTION_DISMISS") {
            val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
            if (notificationId != -1) {
                NotificationManagerCompat.from(context).cancel(notificationId)
            }
            return
        }
        
        if (action == "ACTION_REMIND") {
            val notificationId = intent.getIntExtra("NOTIFICATION_ID", -1)
            if (notificationId != -1) {
                NotificationManagerCompat.from(context).cancel(notificationId)
            }
            
            val remindLater = intent.getIntExtra("REMIND_LATER", 5)
            val label = intent.getStringExtra("LABEL") ?: "Alarm"
            
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val triggerTime = System.currentTimeMillis() + (remindLater * 60 * 1000)
            
            val newIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("LABEL", label)
                putExtra("REMIND_LATER", remindLater)
                if (alarmId != null) putExtra("ALARM_ID", alarmId)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, System.currentTimeMillis().toInt(), newIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
            return
        }

        // Handle the actual alarm trigger
        if (alarmId != null) {
            val alarm = com.example.chintualarm.domain.AlarmRepository.alarmList.value.find { it.id == alarmId }
            if (alarm != null && alarm.weeks.isEmpty()) {
                com.example.chintualarm.domain.AlarmRepository.toggleAlarmState(alarmId, false)
            }
        }

        val label = intent.getStringExtra("LABEL") ?: "Alarm"
        val remindLater = intent.getIntExtra("REMIND_LATER", 5)
        val notificationId = System.currentTimeMillis().toInt()
        
        createNotificationChannel(context)

        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = "ACTION_DISMISS"
            putExtra("NOTIFICATION_ID", notificationId)
            if (alarmId != null) putExtra("ALARM_ID", alarmId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, notificationId, dismissIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val remindIntent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = "ACTION_REMIND"
            putExtra("NOTIFICATION_ID", notificationId)
            putExtra("LABEL", label)
            putExtra("REMIND_LATER", remindLater)
            if (alarmId != null) putExtra("ALARM_ID", alarmId)
        }
        val remindPendingIntent = PendingIntent.getBroadcast(
            context, notificationId + 1, remindIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Chintu Alarm")
            .setContentText(label)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_popup_reminder, "Remind later", remindPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", dismissPendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationId, builder.build())
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Channel"
            val descriptionText = "Channel for Alarm notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarm_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
