// NEW FILE - Generated schedule display screen
package com.example.rat.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rat.model.ScheduleSlot
import com.example.rat.viewmodel.SchedulerViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: SchedulerViewModel,
    onBack: () -> Unit
) {
    val scheduleByDay = viewModel.getScheduleByDay()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Your Schedule",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Round Robin | ${viewModel.timeQuantum} min slots | ${viewModel.breakMinutes} min break | 9:00 AM start",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(16.dp))

        if (scheduleByDay.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No schedule generated yet", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                scheduleByDay.forEach { (dayNumber, slots) ->
                    item(key = "day_$dayNumber") {
                        DayHeader(dayNumber, slots.first().dayDate)
                    }
                    itemsIndexed(slots, key = { _, slot -> slot.slotIndex }) { _, slot ->
                        SlotCard(slot, viewModel.timeQuantum)
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("Back to Tasks", fontSize = 16.sp)
        }
    }
}

@Composable
fun DayHeader(dayNumber: Int, date: java.time.LocalDate) {
    Column {
        Text(
            text = "Day $dayNumber",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = date.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")),
            fontSize = 14.sp,
            color = Color.Gray
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = Color(0xFF6200EE),
            thickness = 2.dp
        )
    }
}

@Composable
fun SlotCard(slot: ScheduleSlot, quantum: Int) {
    val isBreak = slot.isBreak

    // ADDED - Different colors for break vs task slots
    val barColor = if (isBreak) {
        Color(0xFF4CAF50)  // Green for breaks
    } else {
        val progress = slot.durationMinutes.toFloat() / quantum
        when {
            progress >= 1.0f -> Color(0xFF6200EE)
            progress >= 0.5f -> Color(0xFF9C27B0)
            else -> Color(0xFFCE93D8)
        }
    }

    val cardColor = if (isBreak) {
        Color(0xFFE8F5E9)  // Light green background for breaks
    } else {
        Color(0xFFF5F5F5)  // Light gray for tasks
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(barColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${slot.slotIndex + 1}",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isBreak) "Break ☕" else slot.taskName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "${slot.startTime} for ${slot.durationMinutes} min",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            if (!isBreak) {
                LinearProgressIndicator(
                    progress = { slot.durationMinutes.toFloat() / quantum },
                    modifier = Modifier
                        .width(80.dp)
                        .height(8.dp),
                    color = barColor,
                    trackColor = Color(0xFFE0E0E0),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
    }
}
