package com.example.chintualarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import android.os.Build

actual class AlarmScheduler actual constructor() {
    actual fun scheduleAlarm(alarm: AlarmItemDto) {
        val context = AndroidContext.applicationContext ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("LABEL", alarm.label)
            putExtra("REMIND_LATER", alarm.remindLater)
            putExtra("ALARM_SOUND", alarm.alarmSound)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val now = Clock.System.now()
        val tz = TimeZone.currentSystemDefault()
        val localNow = now.toLocalDateTime(tz)

        var triggerDateTime = LocalDateTime(
            year = localNow.year,
            monthNumber = localNow.monthNumber,
            dayOfMonth = localNow.dayOfMonth,
            hour = alarm.hour,
            minute = alarm.minute
        )

        if (triggerDateTime.toInstant(tz) <= now) {
             val tomorrow = now.plus(1, DateTimeUnit.DAY, tz).toLocalDateTime(tz)
             triggerDateTime = LocalDateTime(
                 year = tomorrow.year,
                 monthNumber = tomorrow.monthNumber,
                 dayOfMonth = tomorrow.dayOfMonth,
                 hour = alarm.hour,
                 minute = alarm.minute
             )
        }
        
        val timeInMillis = triggerDateTime.toInstant(tz).toEpochMilliseconds()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        }
    }

    actual fun cancelAlarm(alarmId: String) {
        val context = AndroidContext.applicationContext ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
