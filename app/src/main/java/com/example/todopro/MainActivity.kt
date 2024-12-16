package com.example.todopro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todopro.database.TaskDatabase
import com.example.todopro.repository.TaskRepository
import com.example.todopro.ui.theme.ToDoProTheme

class MainActivity : ComponentActivity() {
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de la clase NotificationHelper
        notificationHelper = NotificationHelper(this)

        setContent {
            ToDoProTheme {
                MainApp()  // Llamada a MainApp sin pasar parámetros
            }
        }

        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001
                )
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()  // Navegación a través del controlador de navegación
    val taskDao = TaskDatabase.getDatabase(LocalContext.current).taskDao()
    val repository = TaskRepository(taskDao)

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(onContinueClicked = {
                navController.navigate("main_menu")
            })
        }
        composable("main_menu") {
            MainMenuScreen(navController = navController)  // Se pasa navController
        }
        composable("task_list/{selectedOption}") { backStackEntry ->
            // Recuperamos el valor de selectedOption desde la URL
            val selectedOption = backStackEntry.arguments?.getString("selectedOption") ?: "Tareas"
            TaskListScreen(repository = repository, navController = navController, selectedOption = selectedOption)  // Pasamos selectedOption
        }
    }
}


@Composable
fun WelcomeScreen(onContinueClicked: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido a ToDoPro",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Retornamos al diseño anterior que estaba funcionando
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

            Button(onClick = { onContinueClicked() }) {
                Text("Continuar")
            }
        }
    }
}

@Composable
fun MainMenuScreen(navController: NavHostController) {
    val options = remember { mutableStateListOf("Tareas de casa", "Pendientes del trabajo", "Lista de compras") }
    var newOption by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¿Dónde desea realizar sus tareas?",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize * 1.1f
            ),
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(options) { option ->
                Button(
                    onClick = {
                        // Navega a la lista de tareas y pasa la opción seleccionada
                        navController.navigate("task_list/${option}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(option)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newOption,
            onValueChange = { newOption = it },
            label = { Text("Nueva opción") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Botón para agregar opción
        Button(
            onClick = {
                if (newOption.isNotEmpty()) {
                    options.add(newOption)
                    newOption = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar otra opción")
        }
    }
}



