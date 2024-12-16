@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todopro

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todopro.database.TaskDatabase
import com.example.todopro.database.TaskEntity
import com.example.todopro.repository.TaskRepository
import com.example.todopro.ui.theme.ToDoProTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskActivity : ComponentActivity() {
    private lateinit var repository: TaskRepository
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskDao = TaskDatabase.getDatabase(this).taskDao()
        repository = TaskRepository(taskDao)
        notificationHelper = NotificationHelper(this)
        // Obtener el título de la tarea desde el intent
        val taskTitle = intent.getStringExtra("taskTitle") ?: ""
        setContent {
            ToDoProTheme {
                // Composicion de la pantalla de agregar tarea
                TaskScreen(repository, taskTitle = taskTitle)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    @Composable
    fun TaskScreen(repository: TaskRepository, taskTitle: String) {
        var title by remember { mutableStateOf(taskTitle) }
        var date by remember { mutableStateOf("") }
        var time by remember { mutableStateOf("") }

        val calendar = Calendar.getInstance()
        val coroutineScope = rememberCoroutineScope()

        // DatePicker and TimePicker dialog setup
        val datePicker = DatePickerDialog(
            LocalContext.current, { _, year, month, dayOfMonth ->
                date = "$dayOfMonth/${month + 1}/$year"
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )

        val timePicker = TimePickerDialog(LocalContext.current, { _, hour, minute ->
            time = String.format("%02d:%02d", hour, minute)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Agregar Tarea") },
                    navigationIcon = {
                        IconButton(onClick = {onBackPressedDispatcher.onBackPressed()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retroceder")
                        }
                    }
                )
            },
            bottomBar = {
                Button(
                    onClick = {
                        if (title.isNotEmpty()) {
                            val newTask = TaskEntity(
                                title = title,
                                date = date,
                                time = time
                            )

                            // Insertar la nueva tarea en la base de datos
                            coroutineScope.launch {
                                repository.insertTask(newTask)
                            }

                            // Programar notificaciones según la fecha y hora seleccionadas
                            val triggerTime = parseDateTime(date, time)
                            if (triggerTime != null) {
                                // Si hay fecha y hora, programamos una notificación exacta
                                notificationHelper.scheduleNotification(
                                    title.hashCode(),
                                    title,
                                    "Recordatorio programado",
                                    triggerTime
                                )
                            } else {
                                // Si no hay fecha ni hora, programar una notificación repetitiva cada 30 minutos
                                notificationHelper.scheduleRepeatingNotification(
                                    title.hashCode(),
                                    title,
                                    "No olvides completar esta tarea."
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        "Guardar Tarea",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Detalles de la Tarea", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la Tarea") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha del Recordatorio") },
                    trailingIcon = {
                        Button(onClick = { datePicker.show() }) { Text("Elegir") }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora del Recordatorio") },
                    trailingIcon = {
                        Button(onClick = { timePicker.show() }) { Text("Elegir") }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    private fun parseDateTime(date: String, time: String): Long? {
        return try {
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val parsedDate = formatter.parse("$date $time")
            parsedDate?.time
        } catch (e: Exception) {
            null
        }
    }
}


