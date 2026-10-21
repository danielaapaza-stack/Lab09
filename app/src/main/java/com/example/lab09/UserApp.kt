package com.example.lab09

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun UsersApp() {
    val servicio = remember {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lista") {
        composable("lista") { ScreenUsuarios(navController, servicio) }
        composable(
            "detalle/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            ScreenDetalleUsuario(navController, servicio, it.arguments!!.getInt("id"))
        }
    }
}

@Composable
fun ScreenUsuarios(navController: NavHostController, servicio: UserApiService) {
    var usuarios by remember { mutableStateOf<List<UserModel>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var busqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            usuarios = servicio.getUsers(100).users
        } catch (e: Exception) {
            error = "No se pudo cargar: ${e.message}"
        }
        cargando = false
    }

    val filtrados = usuarios.filter {
        "${it.firstName} ${it.lastName}".contains(busqueda, ignoreCase = true)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF4A148C), Color(0xFF1A237E))))
    ) {
        Text(
            text = "Directorio de usuarios",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 48.dp, end = 16.dp)
        )
        Text(
            text = "${filtrados.size} personas",
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            placeholder = { Text("Buscar por nombre") },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        when {
            cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
            error != null -> Text(
                text = error ?: "",
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtrados) { usuario ->
                    TarjetaUsuario(usuario) {
                        navController.navigate("detalle/${usuario.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaUsuario(usuario: UserModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = usuario.image,
                contentDescription = usuario.firstName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xFF7C4DFF), CircleShape)
                    .background(Color(0xFFEDE7F6))
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "${usuario.firstName} ${usuario.lastName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1A237E)
                )
                Text(
                    text = usuario.company.title,
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = usuario.email,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = usuario.role.uppercase(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorRol(usuario.role))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

fun colorRol(rol: String): Color = when (rol) {
    "admin" -> Color(0xFFD32F2F)
    "moderator" -> Color(0xFFF57C00)
    else -> Color(0xFF388E3C)
}

@Composable
fun ScreenDetalleUsuario(navController: NavHostController, servicio: UserApiService, id: Int) {
    var usuario by remember { mutableStateOf<UserModel?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id) {
        try {
            usuario = servicio.getUserById(id)
        } catch (e: Exception) {
            error = "No se pudo cargar: ${e.message}"
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF4A148C), Color(0xFF1A237E))))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
        ) {
            Text("← Volver", color = Color.White)
        }

        val u = usuario
        when {
            u != null -> {
                Spacer(Modifier.size(16.dp))
                AsyncImage(
                    model = u.image,
                    contentDescription = u.firstName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .background(Color(0xFFEDE7F6))
                )
                Spacer(Modifier.size(12.dp))
                Text(
                    text = "${u.firstName} ${u.lastName}",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@${u.username}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = u.role.uppercase(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorRol(u.role))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                Spacer(Modifier.size(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        FilaInfo("CORREO", u.email)
                        FilaInfo("TELÉFONO", u.phone)
                        FilaInfo("EDAD", "${u.age} años")
                        FilaInfo("GÉNERO", u.gender)
                        FilaInfo("UNIVERSIDAD", u.university)
                        FilaInfo("UBICACIÓN", "${u.address.city}, ${u.address.state}, ${u.address.country}")
                        FilaInfo("EMPRESA", u.company.name)
                        FilaInfo("CARGO", "${u.company.title} (${u.company.department})")
                    }
                }
            }
            error != null -> Text(text = error ?: "", color = Color.White)
            else -> CircularProgressIndicator(color = Color.White)
        }
    }
}

@Composable
fun FilaInfo(titulo: String, valor: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = titulo,
            color = Color(0xFF7C4DFF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = valor, color = Color(0xFF1A237E), fontSize = 16.sp)
    }
}