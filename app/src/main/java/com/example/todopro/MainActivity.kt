package com.example.todopro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todopro.ui.theme.ToDoProTheme

data class Task(val title: String, val description: String)

class MainActivity : ComponentActivity() {
    private val tasks = mutableStateListOf<Task>()

    // Registrar un callback para manejar el resultado de TaskActivity
    private val addTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val title = result.data?.getStringExtra("task_title") ?: ""
            val description = result.data?.getStringExtra("task_description") ?: ""
            if (title.isNotEmpty() && description.isNotEmpty()) {
                tasks.add(Task(title, description))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoProTheme {
                MainScreen(
                    tasks = tasks,
                    onAddTaskClick = {
                        val intent = Intent(this, TaskActivity::class.java)
                        addTaskLauncher.launch(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(tasks: List<Task>, onAddTaskClick: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Text("+")
            }
        }
    ) { innerPadding ->
        TaskList(tasks = tasks, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun TaskList(tasks: List<Task>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(tasks) { task ->
            TaskItem(task)
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = task.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ToDoProTheme {
        mainScreen()
    }
}

fun mainScreen() {

}
