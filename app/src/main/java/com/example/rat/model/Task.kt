// NEW FILE - Task data model
package com.example.rat.model

import java.time.LocalDate

data class Task(
    val id: Int = 0,
    val name: String,
    val deadline: LocalDate,
    val estimatedMinutes: Int,
    var remainingMinutes: Int = estimatedMinutes
)
