package com.example.todopro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todopro.ui.theme.ToDoProTheme

class TaskListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedOption = intent.getStringExtra("selectedOption") ?: "Tareas"

        setContent {
            ToDoProTheme {
                TaskListScreen(selectedOption)
            }
        }
    }
}

@Composable
fun TaskListScreen(optionTitle: String) {
    val tasks = remember { mutableStateListOf<Task>() }
    val commonTasks = when (optionTitle) {
        "Tareas de casa" -> listOf("Limpiar la casa", "Lavar los platos")
        "Pendientes del trabajo" -> listOf("Enviar informe", "Revisar correos")
        "Lista de compras" -> listOf("Comprar leche", "Comprar pan")
        else -> emptyList()
    }

    var newTaskTitle by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = optionTitle, style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = newTaskTitle,
            onValueChange = { newTaskTitle = it },
            label = { Text("Nueva tarea") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (newTaskTitle.isNotEmpty()) {
                tasks.add(Task(newTaskTitle)) // Agrega la nueva tarea a la lista
                newTaskTitle = "" // Limpia el campo de texto
            }
        }) {
            Text("Agregar Tarea")
        }

        Text(text = "Tareas Comunes", style = MaterialTheme.typography.titleMedium)

        commonTasks.forEach { task ->
            Button(onClick = {
                tasks.add(Task(task))
            }) {
                Text(task)
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(tasks) { task ->
                Text(text = task.title, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
