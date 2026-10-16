package com.example.sistemafarmacia

import android.os.Bundle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sistemafarmacia.ui.theme.SistemaFarmaciaTheme

data class Medicamento(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: String,
    val calificacion: String = "4.8",
    val enOferta: Boolean = false,
    val imagenEmoji: String = "💊",
    val imagenRes: Int? = null
)

data class Categoria(
    val emoji: String,
    val nombre: String
)

// ==========================================
// ACTIVIDAD PRINCIPAL
// ==========================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SistemaFarmaciaTheme {
                PharmacyApp()
            }
        }
    }
}

// ==========================================
// PANTALLA PRINCIPAL
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyApp() {
    var searchQuery by remember { mutableStateOf("") }

    var cartQuantity by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }

// ==========================================
// COLECCIONES DE DATOS
// ==========================================
    val listaCategorias = listOf(
        Categoria("💊", "Medicamentos"),
        Categoria("🍊", "Vitaminas"),
        Categoria("🩹", "Cuidado Personal"),
        Categoria("👶", "Bebé y Maternidad"),
        Categoria("🧴", "Dermocosmética")
    )

    val listaMedicamentos = listOf(
        Medicamento(
            id = 1,
            nombre = "Paracetamol 500mg",
            descripcion = "Analgésico y antipirético para aliviar el dolor y la fiebre.",
            precio = "$4.500",
            calificacion = "4.8",
            enOferta = true,
            imagenEmoji = "💊"
        ),
        Medicamento(
            id = 2,
            nombre = "Ibuprofeno 800mg",
            descripcion = "Antiinflamatorio indicado para dolores fuertes e inflamación.",
            precio = "$8.200",
            calificacion = "4.6",
            enOferta = false,
            imagenEmoji = "💊"
        ),
        Medicamento(
            id = 3,
            nombre = "Amoxicilina 500mg",
            descripcion = "Antibiótico para combatir infecciones bacterianas.",
            precio = "$15.900",
            calificacion = "4.9",
            enOferta = true,
            imagenEmoji = "🧪"
        ),
        Medicamento(
            id = 4,
            nombre = "Omeprazol 20mg",
            descripcion = "Tratamiento para la acidez estomacal y gastritis.",
            precio = "$11.300",
            calificacion = "4.7",
            enOferta = false,
            imagenEmoji = "💊"
        ),
        Medicamento(
            id = 5,
            nombre = "Loratadina 10mg",
            descripcion = "Antihistamínico para el alivio de alergias y rinitis.",
            precio = "$6.800",
            calificacion = "4.5",
            enOferta = true,
            imagenEmoji = "🌿"
        )
    )


    val medicamentosFiltrados = listaMedicamentos.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) ||
                it.descripcion.contains(searchQuery, ignoreCase = true)
    }

    fun increaseQuantity(medicamentoId: Int) {
        val currentQuantity = cartQuantity[medicamentoId] ?: 0

        cartQuantity = cartQuantity + (medicamentoId to (currentQuantity + 1))

    }

    fun decreaseQuantity(medicamentoId: Int) {
        val currentQuantity = cartQuantity[medicamentoId] ?: 0

        cartQuantity = if (currentQuantity > 1) {
            cartQuantity + (medicamentoId to (currentQuantity - 1))
        } else {
            cartQuantity - medicamentoId
        }
    }

    val cartCount = cartQuantity.values.sum()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "GestorPharma",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("🏠") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("🛒") },
                    label = { Text("Carrito ($cartCount)") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {},
                    icon = { Text("👤") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ENCABEZADO
            item {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hola 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "¿Qué necesitas hoy?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // BUSCADOR
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar medicamento") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // SECCIÓN DE CATEGORÍAS
            item {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Categorías",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listaCategorias) { cat ->
                            CategoryCard(categoria = cat)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Medicamentos disponibles",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (medicamentosFiltrados.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron productos",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(medicamentosFiltrados, key = { it.id }) { medicamento ->
                    val quantity = cartQuantity[medicamento.id] ?: 0
                    ProductCard(
                        medicamento = medicamento,
                        quantity = quantity,
                        onIncrease = { increaseQuantity(medicamento.id) },
                        onDecrease = { decreaseQuantity(medicamento.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ==========================================
// CARD PARA CATEGORÍA
// ==========================================
@Composable
fun CategoryCard(categoria: Categoria) {
    Card {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = categoria.emoji)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = categoria.nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ==========================================
// CARD PARA MEDICAMENTO
// ==========================================
/*
* Si la cantidad es 0, muestra agregar
* al presionar agregar , la cantidad pasa a 1
* Cuando la cantidad es mayor que cero, aparece - y +
* Al presionar +, la cantidad aumenta
* Al presionar -, la cantidad disminuye
* Cuando la cantidad es 0, vuelve a aparecer agregar
* En la barra inferior se ve la suma de las cantidades de todos los productos
* */

@Composable
fun ProductCard(
    medicamento: Medicamento,
    quantity: Int = 0,
    onIncrease: () -> Unit = {},
    onDecrease: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (medicamento.imagenRes != null) {
                    Image(
                        painter = painterResource(id = medicamento.imagenRes),
                        contentDescription = medicamento.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = medicamento.imagenEmoji,
                        fontSize = 32.sp
                    )
                }

                if (medicamento.enOferta) {
                    Surface(
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 8.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "Oferta",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onError,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Detalle del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = medicamento.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "⭐ ${medicamento.calificacion}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = medicamento.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = medicamento.precio,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (quantity == 0) {
                        Button(
                            onClick = onIncrease,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Agregar",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDecrease,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(text = "-")
                            }
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            OutlinedButton(
                                onClick = onIncrease,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(text = "+")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// PREVISUALIZACIÓN
// ==========================================
@Preview(showBackground = true)
@Composable
fun PharmacyAppPreview() {
    SistemaFarmaciaTheme {
        PharmacyApp()
    }
}
