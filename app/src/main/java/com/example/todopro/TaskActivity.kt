package com.example.todopro

import android.content.Context
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.todopro.ui.theme.ToDoProTheme
import java.util.concurrent.TimeUnit

class TaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ToDoProTheme {
                TaskScreen()
            }
        }
    }
}

@Composable
fun TaskScreen() {
    val context = LocalContext.current // Obtener el contexto actual

    TaskForm { title, reminderDate, reminderTime ->
        // Lógica para manejar la tarea añadida
        if (title.isNotEmpty()) {
            // Aquí puedes agregar la tarea a tu lista o base de datos

            // Si no hay fecha y hora, configurar recordatorio cada 30 minutos
            if (reminderTime != null) {
                if (reminderDate != null) {
                    if (reminderDate.isEmpty() && reminderTime.isEmpty()) {
                        scheduleNotification(title, context) // Usar el contexto actual aquí
                    } else {
                        // Implementa la lógica para manejar recordatorios específicos con fecha y hora aquí.
                        // scheduleNotificationWithDateAndTime(title, reminderDate, reminderTime)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskForm(onSave: (String, String?, String?) -> Unit) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var reminderDate by remember { mutableStateOf(TextFieldValue("")) }
    var reminderTime by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Añadir Tarea", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = reminderDate,
            onValueChange = { reminderDate = it },
            label = { Text("Fecha del Recordatorio (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = reminderTime,
            onValueChange = { reminderTime = it },
            label = { Text("Hora del Recordatorio (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSave(title.text,
                    reminderDate.text.ifEmpty { null },
                    reminderTime.text.ifEmpty { null })
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Guardar")
        }

        // Lista de tareas comunes
        Text(text = "Tareas Comunes:", style = MaterialTheme.typography.titleMedium)
        val commonTasks = listOf("Limpiar la casa", "Hacer la compra", "Estudiar para el examen")

        for (task in commonTasks) {
            Button(onClick = {
                title = TextFieldValue(task) // Rellenar el título con la tarea común seleccionada
            }) {
                Text(task)
            }
        }
    }
}

// Función para programar notificaciones cada 30 minutos usando WorkManager
fun scheduleNotification(taskTitle: String, context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(30, TimeUnit.MINUTES)
        .setInputData(workDataOf("TASK_TITLE" to taskTitle))
        .build()

    WorkManager.getInstance(context).enqueue(workRequest) // Usa el contexto correcto aquí.
}

// Worker para manejar la notificación
class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        inputData.getString("TASK_TITLE") ?: return Result.failure()

        // Aquí implementa la lógica para mostrar la notificación con el título de la tarea.

        return Result.success() // Asegúrate de devolver un resultado correcto.
    }
}