Carrito de Compra y otro para Productos Favoritos. Serán muy útiles y fáciles de entender.
1. CarritoSingleton.kt (Gestiona el carrito de compra)
Crea en data/CarritoSingleton.kt:
kotlin
Copy
package com.example.ejercicioclase.data

// SINGLETON: Solo existe un carrito en toda la app
object CarritoSingleton {

    // Lista de productos en el carrito (mutable)
    private val _productos = mutableListOf<ProductoCarrito>()

    // Versión de solo lectura para acceder desde fuera
    val productos: List<ProductoCarrito> get() = _productos.toList()

    // Cantidad total de items
    val cantidadTotal: Int get() = _productos.sumOf { it.cantidad }

    // Precio total del carrito
    val precioTotal: Double get() = _productos.sumOf { it.precio * it.cantidad }

    // Añadir producto al carrito
    fun agregarProducto(producto: ProductoCarrito) {
        val existente = _productos.find { it.id == producto.id }

        if (existente != null) {
            // Si ya existe, aumentar cantidad
            existente.cantidad += producto.cantidad
        } else {
            // Si no existe, añadir nuevo
            _productos.add(producto)
        }
    }

    // Eliminar producto del carrito
    fun eliminarProducto(id: Int) {
        _productos.removeAll { it.id == id }
    }

    // Cambiar cantidad de un producto
    fun actualizarCantidad(id: Int, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            eliminarProducto(id)
        } else {
            _productos.find { it.id == id }?.cantidad = nuevaCantidad
        }
    }

    // Vaciar carrito completo
    fun vaciarCarrito() {
        _productos.clear()
    }

    // Verificar si un producto está en el carrito
    fun estaEnCarrito(id: Int): Boolean {
        return _productos.any { it.id == id }
    }
}

// Data class para items del carrito
data class ProductoCarrito(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val imagen: Int,
    var cantidad: Int = 1
)
2. FavoritosSingleton.kt (Gestiona productos favoritos)
Crea en data/FavoritosSingleton.kt:
kotlin
Copy
package com.example.ejercicioclase.data

// SINGLETON: Solo existe una lista de favoritos en toda la app
object FavoritosSingleton {

    // Lista de IDs de productos favoritos
    private val _favoritos = mutableSetOf<Int>()

    // Versión de solo lectura
    val favoritos: Set<Int> get() = _favoritos.toSet()

    // Contador de favoritos
    val cantidad: Int get() = _favoritos.size

    // Añadir a favoritos
    fun agregarFavorito(idProducto: Int) {
        _favoritos.add(idProducto)
    }

    // Eliminar de favoritos
    fun eliminarFavorito(idProducto: Int) {
        _favoritos.remove(idProducto)
    }

    // Alternar favorito (añadir si no está, quitar si está)
    fun toggleFavorito(idProducto: Int): Boolean {
        return if (_favoritos.contains(idProducto)) {
            _favoritos.remove(idProducto)
            false // Ya no es favorito
        } else {
            _favoritos.add(idProducto)
            true // Ahora es favorito
        }
    }

    // Verificar si es favorito
    fun esFavorito(idProducto: Int): Boolean {
        return _favoritos.contains(idProducto)
    }

    // Vaciar todos los favoritos
    fun vaciarFavoritos() {
        _favoritos.clear()
    }
}
3. ProductCard mejorada (con botón de favorito y añadir al carrito)
Actualiza o crea componentes/ProductoCard.kt:
kotlin
Copy
package com.example.ejercicioclase.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ejercicioclase.R
import com.example.ejercicioclase.data.CarritoSingleton
import com.example.ejercicioclase.data.FavoritosSingleton
import com.example.ejercicioclase.data.ProductoCarrito

@Composable
fun ProductoCard(
    id: Int,
    nombre: String,
    precio: Double,
    imagen: Int = R.drawable.ic_launcher_foreground
) {
    // Estado del favorito (observa el Singleton)
    var esFavorito by remember { mutableStateOf(FavoritosSingleton.esFavorito(id)) }

    Card(
        modifier = Modifier
            .width(160.dp)
            .padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen con botón de favorito encima
            Box {
                Image(
                    painter = painterResource(imagen),
                    contentDescription = nombre,
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                )

                // Botón favorito (esquina superior derecha)
                IconButton(
                    onClick = {
                        esFavorito = FavoritosSingleton.toggleFavorito(id)
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (esFavorito) Color.Red else Color.Gray
                    )
                }
            }

            // Nombre del producto
            Text(
                text = nombre,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // Precio
            Text(
                text = "%.2f€".format(precio),
                color = Color.Green,
                fontWeight = FontWeight.Medium
            )

            // Botón añadir al carrito
            Button(
                onClick = {
                    CarritoSingleton.agregarProducto(
                        ProductoCarrito(
                            id = id,
                            nombre = nombre,
                            precio = precio,
                            imagen = imagen,
                            cantidad = 1
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Añadir")
            }
        }
    }
}

// ============================================
// PREVIEW
// ============================================

@Preview(showBackground = true)
@Composable
fun ProductoCardPreview() {
    MaterialTheme {
        ProductoCard(
            id = 1,
            nombre = "Producto Ejemplo",
            precio = 19.99
        )
    }
}

@Preview(showBackground = true, name = "Producto Favorito")
@Composable
fun ProductoCardFavoritoPreview() {
    MaterialTheme {
        // Simulamos que es favorito
        FavoritosSingleton.agregarFavorito(2)
        ProductoCard(
            id = 2,
            nombre = "Producto Favorito",
            precio = 29.99
        )
    }
}
4. CartScreen.kt mejorada (usa el Singleton)
kotlin
Copy
package com.example.ejercicioclase.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ejercicioclase.data.CarritoSingleton
import com.example.ejercicioclase.data.ProductoCarrito

@Composable
fun CartScreen(navController: NavController) {

    // Observamos el carrito (se actualiza automáticamente)
    var productos by remember { mutableStateOf(CarritoSingleton.productos) }

    // Función para refrescar la lista
    fun refrescarCarrito() {
        productos = CarritoSingleton.productos.toList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Carrito de Compra (${CarritoSingleton.cantidadTotal})",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (productos.isEmpty()) {
            // Carrito vacío
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tu carrito está vacío",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // Lista de productos
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productos, key = { it.id }) { producto ->
                    CarritoItem(
                        producto = producto,
                        onCantidadChange = {
                            refrescarCarrito()
                        },
                        onEliminar = {
                            refrescarCarrito()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "%.2f€".format(CarritoSingleton.precioTotal),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Green
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    CarritoSingleton.vaciarCarrito()
                    refrescarCarrito()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text("Vaciar Carrito")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* TODO: Realizar pedido */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Realizar Pedido")
            }
        }
    }
}

@Composable
fun CarritoItem(
    producto: ProductoCarrito,
    onCantidadChange: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(producto.imagen),
                contentDescription = producto.nombre,
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "%.2f€".format(producto.precio),
                    color = Color.Green
                )
            }

            // Controles de cantidad
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        CarritoSingleton.actualizarCantidad(
                            producto.id,
                            producto.cantidad - 1
                        )
                        onCantidadChange()
                    }
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Restar")
                }

                Text(
                    text = producto.cantidad.toString(),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = {
                        CarritoSingleton.actualizarCantidad(
                            producto.id,
                            producto.cantidad + 1
                        )
                        onCantidadChange()
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Sumar")
                }
            }

            // Botón eliminar
            IconButton(
                onClick = {
                    CarritoSingleton.eliminarProducto(producto.id)
                    onEliminar()
                }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }
        }
    }
}

// ============================================
// PREVIEWS
// ============================================

@Preview(showBackground = true, showSystemUi = true, name = "Carrito con items")
@Composable
fun CartScreenPreview() {
    // Añadimos items de ejemplo al Singleton
    CarritoSingleton.agregarProducto(
        ProductoCarrito(1, "Producto A", 15.99, R.drawable.ic_launcher_foreground, 2)
    )
    CarritoSingleton.agregarProducto(
        ProductoCarrito(2, "Producto B", 8.50, R.drawable.ic_launcher_foreground, 1)
    )

    MaterialTheme {
        CartScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Carrito vacío")
@Composable
fun CartScreenEmptyPreview() {
    // Nos aseguramos de que esté vacío
    CarritoSingleton.vaciarCarrito()

    MaterialTheme {
        CartScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun CarritoItemPreview() {
    MaterialTheme {
        CarritoItem(
            producto = ProductoCarrito(
                id = 1,
                nombre = "Producto Ejemplo",
                precio = 12.99,
                imagen = R.drawable.ic_launcher_foreground,
                cantidad = 2
            ),
            onCantidadChange = {},
            onEliminar = {}
        )
    }
}

5. HomeScreen.kt actualizada (usa las nuevas cards)
Actualiza la parte del carrusel de productos en HomeScreen.kt:

// Reemplaza ProductCard por ProductoCard
@Composable
fun ProductsCarousel() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(5) { index ->
            ProductoCard(
                id = index,
                nombre = "Producto ${index + 1}",
                precio = (10 + index * 5).toDouble()
            )
        }
    }
}
No olvides importar:

import com.example.ejercicioclase.ui.componentes.ProductoCard

6. BottomNav con badge de carrito (opcional, queda muy profesional)
Actualiza BottomNav en HomeScreen.kt para mostrar la cantidad:

@Composable
fun BottomNav(navController: NavController, modifier: Modifier = Modifier) {

    // Observamos el carrito para el badge
    val cantidadCarrito = CarritoSingleton.cantidadTotal

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(Color.Gray)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(
            icon = R.drawable.ic_launcher_foreground,
            label = "Home",
            onClick = { }
        )

        NavItem(
            icon = R.drawable.ic_launcher_foreground,
            label = "Vistos",
            onClick = { navController.navigate("history") }
        )

        // Carrito con badge si hay items
        BadgedBox(
            badge = {
                if (cantidadCarrito > 0) {
                    Badge { Text(cantidadCarrito.toString()) }
                }
            }
        ) {
            NavItem(
                icon = R.drawable.ic_launcher_foreground,
                label = "Carrito",
                onClick = { navController.navigate("cart") }
            )
        }

        NavItem(
            icon = R.drawable.ic_launcher_foreground,
            label = "Perfil",
            onClick = { navController.navigate("profile") }
        )
    }
}
Añade el import:

import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge