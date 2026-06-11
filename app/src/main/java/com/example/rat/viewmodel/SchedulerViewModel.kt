
package com.example.rat.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rat.model.ScheduleSlot
import com.example.rat.model.Task
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class SchedulerViewModel : ViewModel() {

    private val _tasks = mutableListOf<Task>()
    val tasks: List<Task> get() = _tasks

    private var nextId = 1

    private val _schedule = mutableListOf<ScheduleSlot>()
    val schedule: List<ScheduleSlot> get() = _schedule

    val timeQuantum = 25
    val studyHoursPerDay = 8
    val dailyMinutes = studyHoursPerDay * 60
    var breakMinutes = 5

    fun addTask(name: String, deadline: LocalDate, estimatedMinutes: Int) {
        _tasks.add(
            Task(
                id = nextId++,
                name = name,
                deadline = deadline,
                estimatedMinutes = estimatedMinutes,
                remainingMinutes = estimatedMinutes
            )
        )
    }

    fun removeTask(task: Task) {
        _tasks.remove(task)
    }

    fun generateSchedule(): List<ScheduleSlot> {
        _schedule.clear()
        if (_tasks.isEmpty()) return _schedule

        val today = LocalDate.now()
        val latestDeadline = _tasks.maxOf { it.deadline }

        val totalDays = ChronoUnit.DAYS.between(today, latestDeadline).toInt() + 1
        if (totalDays <= 0) return _schedule

        val queue = _tasks.map {
            it.copy(remainingMinutes = it.estimatedMinutes)
        }.toMutableList()

        var slotIndex = 0
        var currentDay = 0

        while (queue.isNotEmpty() && currentDay < totalDays) {
            var dayMinutesLeft = dailyMinutes
            val dayDate = today.plusDays(currentDay.toLong())

            while (dayMinutesLeft > 0 && queue.isNotEmpty()) {
                val task = queue.removeFirst()

                val workMinutes = minOf(timeQuantum, dayMinutesLeft, task.remainingMinutes)
                val startTime = calculateStartTime(dayDate, dailyMinutes - dayMinutesLeft)

                _schedule.add(
                    ScheduleSlot(
                        taskName = task.name,
                        dayNumber = currentDay + 1,
                        dayDate = dayDate,
                        startTime = startTime,
                        durationMinutes = workMinutes,
                        slotIndex = slotIndex++
                    )
                )

                dayMinutesLeft -= workMinutes
                task.remainingMinutes -= workMinutes

                // ADDED - Add break slot after each task slot
                if (dayMinutesLeft >= breakMinutes && breakMinutes > 0) {
                    val breakStartTime = calculateStartTime(dayDate, dailyMinutes - dayMinutesLeft)
                    _schedule.add(
                        ScheduleSlot(
                            taskName = "Break",
                            dayNumber = currentDay + 1,
                            dayDate = dayDate,
                            startTime = breakStartTime,
                            durationMinutes = breakMinutes,
                            slotIndex = slotIndex++,
                            isBreak = true
                        )
                    )
                    dayMinutesLeft -= breakMinutes
                }

                if (task.remainingMinutes > 0) {
                    queue.add(task)
                }
            }

            currentDay++
        }

        return _schedule
    }

    private fun calculateStartTime(dayDate: LocalDate, minutesElapsed: Int): String {
        val startTime = LocalTime.of(9, 0).plusMinutes(minutesElapsed.toLong())
        return startTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    }

    fun getScheduleByDay(): Map<Int, List<ScheduleSlot>> {
        return _schedule.groupBy { it.dayNumber }
    }

    fun getTotalEstimatedMinutes(): Int {
        return _tasks.sumOf { it.estimatedMinutes }
    }

    fun getDaysUntilDeadline(): Int {
        if (_tasks.isEmpty()) return 0
        val today = LocalDate.now()
        val latestDeadline = _tasks.maxOf { it.deadline }
        return (ChronoUnit.DAYS.between(today, latestDeadline) + 1).toInt()
    }

    fun canFitSchedule(): Boolean {
        return getTotalEstimatedMinutes() <= getDaysUntilDeadline() * dailyMinutes
    }
}
