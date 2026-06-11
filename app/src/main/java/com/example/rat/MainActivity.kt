
package com.example.rat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rat.screens.AddTaskScreen
import com.example.rat.screens.HomeScreen
import com.example.rat.screens.ScheduleScreen
import com.example.rat.ui.theme.RATTheme
import com.example.rat.viewmodel.SchedulerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RATTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SchedulerApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SchedulerApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel: SchedulerViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onAddTask = { navController.navigate("add_task") },
                onGenerateSchedule = {
                    viewModel.generateSchedule()
                    navController.navigate("schedule")
                }
            )
        }

        composable("add_task") {
            AddTaskScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("schedule") {
            ScheduleScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
