package com.example.chintualarm

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNNotificationSound
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound

actual class AlarmScheduler actual constructor() {
    actual fun scheduleAlarm(alarm: AlarmItemDto) {
        val content = UNMutableNotificationContent()
        content.setTitle("Chintu Alarm")
        content.setBody(alarm.label ?: "Alarm")
        content.setSound(UNNotificationSound.defaultSound)
        
        val dateComponents = NSDateComponents()
        dateComponents.hour = alarm.hour.toLong()
        dateComponents.minute = alarm.minute.toLong()
        
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(dateComponents, repeats = false)
        

        val request = UNNotificationRequest.requestWithIdentifier(alarm.id, content, trigger)
        
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(UNAuthorizationOptionAlert or UNAuthorizationOptionSound) { granted, authError ->
            if (granted) {
                center.addNotificationRequest(request) { error ->
                    if (error != null) {
                        println("Error scheduling notification: $error")
                    }
                }
            } else {
                println("Notification permission denied: $authError")
            }
        }
    }

    actual fun cancelAlarm(alarmId: String) {
        UNUserNotificationCenter.currentNotificationCenter().removePendingNotificationRequestsWithIdentifiers(listOf(alarmId))
    }
}
