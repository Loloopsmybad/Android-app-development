// NEW FILE - Add task form screen

package com.example.rat.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rat.viewmodel.SchedulerViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: SchedulerViewModel,
    onBack: () -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    var deadlineText by remember { mutableStateOf("") }
    var hoursText by remember { mutableStateOf("") }
    var minutesText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

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
                text = "Add Task",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Task Name") },
            placeholder = { Text("e.g. Study Math") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = deadlineText,
            onValueChange = { deadlineText = it },
            label = { Text("Deadline (yyyy-MM-dd)") },
            placeholder = { Text(" 2026-06-30") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Estimated Time",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = hoursText,
                onValueChange = { hoursText = it },
                label = { Text("Hours") },
                placeholder = { Text("0") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = minutesText,
                onValueChange = { minutesText = it },
                label = { Text("Minutes") },
                placeholder = { Text("0") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(text = errorMessage, color = Color(0xFFC62828), fontSize = 14.sp)
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                val name = taskName.trim()
                val deadlineStr = deadlineText.trim()
                val hours = hoursText.trim().toIntOrNull() ?: 0
                val mins = minutesText.trim().toIntOrNull() ?: 0
                val totalMinutes = hours * 60 + mins

                when {
                    name.isEmpty() -> errorMessage = "Enter a task name"
                    deadlineStr.isEmpty() -> errorMessage = "Enter a deadline"
                    totalMinutes <= 0 -> errorMessage = "Enter estimated time"
                    else -> {
                        try {
                            val deadline = LocalDate.parse(deadlineStr, DateTimeFormatter.ISO_LOCAL_DATE)
                            if (deadline.isBefore(LocalDate.now())) {
                                errorMessage = "Deadline can't be in the past"
                            } else {
                                viewModel.addTask(name, deadline, totalMinutes)
                                onBack()
                            }
                        } catch (e: DateTimeParseException) {
                            errorMessage = "Use date format: yyyy-MM-dd (e.g. 2026-06-30)"
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("Add Task", fontSize = 16.sp)
        }
    }
}
