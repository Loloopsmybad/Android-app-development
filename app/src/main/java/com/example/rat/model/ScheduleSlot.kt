// NEW FILE - Schedule slot data model
package com.example.rat.model

import java.time.LocalDate

data class ScheduleSlot(
    val taskName: String,
    val dayNumber: Int,
    val dayDate: LocalDate,
    val startTime: String,
    val durationMinutes: Int,
    val slotIndex: Int,
    val isBreak: Boolean = false
)
