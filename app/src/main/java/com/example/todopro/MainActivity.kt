package com.example.todopro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.todopro.ui.theme.ToDoProTheme

data class Task(val title: String, val reminderDate: String? = null, val reminderTime: String? = null)

class MainActivity : ComponentActivity() {
    private val tasks = mutableStateListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoProTheme {
                AppNavigation(tasks = tasks)
            }
        }

        Toast.makeText(this, "Hola, suerte al conseguir sus objetivos", Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        println("onStart")
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
    }

    override fun onPause() {
        super.onPause()
        println("onPause")
    }

    override fun onStop() {
        super.onStop()
        println("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy")
    }
}

@Composable
fun AppNavigation(tasks: MutableList<Task>) {
    var showWelcomeScreen by remember { mutableStateOf(true) }

    if (showWelcomeScreen) {
        WelcomeScreen(onContinueClicked = { showWelcomeScreen = false })
    } else {
        MainMenuScreen(tasks = tasks)
    }
}

@Composable
fun WelcomeScreen(onContinueClicked: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido a ToDoPro",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_check),
                    contentDescription = "Icono de tareas"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crea tareas de forma rápida y sencilla")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_notifications),
                    contentDescription = "Icono de recordatorios"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Recordatorio de tus tareas")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onContinueClicked) {
                Text("Continuar")
            }
        }
    }
}

@Composable
fun MainMenuScreen(tasks: MutableList<Task>) {
    val options = listOf("Tareas de casa", "Pendientes del trabajo", "Lista de compras")
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Que tus tareas tengan orden", style = MaterialTheme.typography.headlineMedium)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(options) { option ->
                Button(onClick = {
                    val intent = Intent(context, TaskListActivity::class.java).apply {
                        putExtra("selectedOption", option)
                    }
                    context.startActivity(intent)
                }) {
                    Text(option)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

