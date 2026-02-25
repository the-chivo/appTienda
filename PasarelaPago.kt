Creamos un Singleton para guardar la configuración del usuario (modo oscuro, idioma, etc.).
1. Crea ConfiguracionGlobal.kt en data/
kotlin
Copy
package com.example.ejercicioclase.data

// SINGLETON: Solo existe UNA instancia durante toda la app
object ConfiguracionGlobal {

    // Configuración de la app
    var modoOscuro: Boolean = false
    var idioma: String = "es"
    var notificacionesActivadas: Boolean = true

    // Datos del usuario logueado (null si no hay nadie)
    var usuarioActual: String? = null

    // Función para cerrar sesión
    fun cerrarSesion() {
        usuarioActual = null
    }

    // Función para guardar usuario
    fun iniciarSesion(username: String) {
        usuarioActual = username
    }
}
2. Úsalo en LoginScreen.kt
kotlin
Copy
package com.example.ejercicioclase.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ejercicioclase.data.ConfiguracionGlobal  // ← IMPORTA EL SINGLETON
import com.example.ejercicioclase.data.LoginUiState

@Composable
fun LoginScreen(navController: NavController) {
    var uiState by remember { mutableStateOf(LoginUiState()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = uiState.username,
            onValueChange = {
                uiState = uiState.copy(username = it, errorMessage = null)
            },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = uiState.password,
            onValueChange = {
                uiState = uiState.copy(password = it, errorMessage = null)
            },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = uiState.errorMessage!!, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (uiState.username.isNotBlank() && uiState.password.isNotBlank()) {

                    // ← USAMOS EL SINGLETON AQUÍ
                    ConfiguracionGlobal.iniciarSesion(uiState.username)

                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    uiState = uiState.copy(errorMessage = "Completa todos los campos")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(navController = rememberNavController())
    }
}
3. Úsalo en ProfileScreen.kt para mostrar el usuario
kotlin
Copy
package com.example.ejercicioclase.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ejercicioclase.R
import com.example.ejercicioclase.data.ConfiguracionGlobal  // ← IMPORTA EL SINGLETON

@Composable
fun ProfileScreen(navController: NavController) {

    // ← OBTENEMOS EL USUARIO DEL SINGLETON
    val nombreUsuario = ConfiguracionGlobal.usuarioActual ?: "Invitado"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "Foto de perfil",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ← USAMOS EL NOMBRE DEL SINGLETON
        Text(
            text = nombreUsuario,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "$nombreUsuario@email.com",
            style = MaterialTheme.typography.bodyLarge,
            color = androidx.compose.ui.graphics.Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Mostrar configuración del Singleton
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(label = "Idioma", value = ConfiguracionGlobal.idioma)
                InfoRow(
                    label = "Modo Oscuro",
                    value = if (ConfiguracionGlobal.modoOscuro) "Activado" else "Desactivado"
                )
                InfoRow(
                    label = "Notificaciones",
                    value = if (ConfiguracionGlobal.notificacionesActivadas) "Sí" else "No"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("settings") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Configuración")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // ← USAMOS EL SINGLETON PARA CERRAR SESIÓN
                ConfiguracionGlobal.cerrarSesion()

                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar Sesión")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = androidx.compose.ui.graphics.Color.Gray)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(navController = rememberNavController())
    }
}
4. Úsalo en SettingsScreen.kt para modificar la configuración
kotlin
Copy
package com.example.ejercicioclase.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ejercicioclase.data.ConfiguracionGlobal  // ← IMPORTA EL SINGLETON

@Composable
fun SettingsScreen(navController: NavController) {

    // ← ESTADO DEL SINGLETON (se actualiza en toda la app)
    var modoOscuro by remember { mutableStateOf(ConfiguracionGlobal.modoOscuro) }
    var notificaciones by remember { mutableStateOf(ConfiguracionGlobal.notificacionesActivadas) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Notificaciones - Guardado en Singleton
        SettingItemWithSwitch(
            icon = Icons.Default.Notifications,
            title = "Notificaciones",
            description = "Recibir alertas de ofertas",
            checked = notificaciones,
            onCheckedChange = {
                notificaciones = it
                ConfiguracionGlobal.notificacionesActivadas = it  // ← GUARDA EN SINGLETON
            }
        )

        // Modo Oscuro - Guardado en Singleton
        SettingItemWithSwitch(
            icon = Icons.Default.Settings,
            title = "Modo Oscuro",
            description = "Cambiar tema de la app",
            checked = modoOscuro,
            onCheckedChange = {
                modoOscuro = it
                ConfiguracionGlobal.modoOscuro = it  // ← GUARDA EN SINGLETON
            }
        )

        // Idioma
        SettingItem(
            icon = Icons.Default.Menu,
            title = "Idioma",
            description = "Español"
        )

        // Privacidad
        SettingItem(
            icon = Icons.Default.Lock,
            title = "Privacidad",
            description = "Gestionar permisos"
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Acerca de
        SettingItem(
            icon = Icons.Default.Info,
            title = "Acerca de",
            description = "Versión 1.0.0"
        )

        // Cerrar sesión
        SettingItem(
            icon = Icons.Default.ExitToApp,
            title = "Cerrar Sesión",
            description = "Salir de la cuenta",
            isDestructive = true,
            onClick = {
                ConfiguracionGlobal.cerrarSesion()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isDestructive) MaterialTheme.colorScheme.error
                   else MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDestructive) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Ir",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingItemWithSwitch(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(navController = rememberNavController())
    }
}
Resumen: ¿Dónde usamos el Singleton?
Table
Copy
Pantalla	Uso del Singleton
Login	Guarda el usuario logueado
Perfil	Muestra el usuario y la configuración
Ajustes	Modifica modo oscuro y notificaciones
Cualquier parte	Accede a ConfiguracionGlobal.usuarioActual
¿Por qué es Singleton?
kotlin
Copy
// En cualquier archivo, SIEMPRE es la MISMA instancia:
ConfiguracionGlobal.usuarioActual = "Juan"  // En Login

// En otra pantalla, sigue siendo "Juan":
println(ConfiguracionGlobal.usuarioActual)  // Imprime: Juan

// No hace falta crearlo con "new", ya existe:
// val config = ConfiguracionGlobal()  // ❌ ERROR, no tiene constructor