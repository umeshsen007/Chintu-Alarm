package com.example.chintualarm

import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute

actual fun getCurrentHourAndMinute(): Pair<Int, Int> {
    val date = NSDate()
    val calendar = NSCalendar.currentCalendar
    val components = calendar.components(NSCalendarUnitHour or NSCalendarUnitMinute, fromDate = date)
    return Pair(components.hour.toInt(), components.minute.toInt())
}
