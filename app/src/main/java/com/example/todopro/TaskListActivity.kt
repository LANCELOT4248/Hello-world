@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todopro

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.todopro.database.TaskDatabase
import com.example.todopro.database.TaskEntity
import com.example.todopro.repository.TaskRepository
import com.example.todopro.ui.theme.ToDoProTheme
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment


class TaskListActivity : ComponentActivity() {
    private lateinit var repository: TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskDao = TaskDatabase.getDatabase(this).taskDao()
        repository = TaskRepository(taskDao)

        setContent {
            ToDoProTheme {
                val navController = rememberNavController()
                // Aquí se obtiene el parámetro 'selectedOption' de la navegación
                val selectedOption = intent.getStringExtra("selectedOption") ?: "Tareas"
                TaskListScreen(repository = repository, navController = navController, selectedOption = selectedOption)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(repository: TaskRepository, navController: NavHostController, selectedOption: String) {
    val tasks by repository.allTasks.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }

    // Obtener las tareas comunes
    val commonTasks = when (selectedOption) {
        "Tareas de casa" -> listOf("Limpiar la casa", "Lavar los platos")
        "Pendientes del trabajo" -> listOf("Enviar informe", "Revisar correos")
        "Lista de compras" -> listOf("Comprar leche", "Comprar pan")
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Tareas") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp() // Navega hacia atrás
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retroceder")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val intent = Intent(context, TaskActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar tarea")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Mostrar Tareas comunes
            if (commonTasks.isNotEmpty()) {
                Text("Tareas comunes", style = MaterialTheme.typography.titleMedium)
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(commonTasks) { taskTitle ->
                        Button(
                            onClick = {
                                val intent = Intent(context, TaskActivity::class.java).apply {
                                    putExtra("taskTitle", taskTitle)
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(taskTitle)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar Tareas específicas
            Text("Tareas específicas", style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(tasks) { task ->
                    TaskListItem(
                        task = task,
                        onComplete = {
                            coroutineScope.launch {
                                repository.updateTask(task.copy(isCompleted = true))
                                notificationHelper.sendImmediateNotification(
                                    task.id,
                                    "¡Tarea Completada!",
                                    "Completaste: ${task.title}"
                                )
                            }
                        },
                        onDelete = {
                            coroutineScope.launch {
                                repository.deleteTask(task)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskListItem(
    task: TaskEntity,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge
            )
            Row {
                IconButton(onClick = onComplete) {
                    Icon(Icons.Default.Check, contentDescription = "Completar")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}



