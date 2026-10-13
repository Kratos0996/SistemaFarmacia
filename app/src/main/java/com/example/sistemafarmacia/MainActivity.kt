package com.example.sistemafarmacia

import android.os.Bundle
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    Medicamento("Paracetamol 500mg", "Analgésico y antipirético para aliviar el dolor y la fiebre.", "$4.500", "4.8", true, "💊"),
    Medicamento("Ibuprofeno 800mg", "Antiinflamatorio indicado para dolores fuertes e inflamación.", "$8.200", "4.6", false, "💊"),
    Medicamento("Amoxicilina 500mg", "Antibiótico para combatir infecciones bacterianas.", "$15.900", "4.9", true, "🧪"),
    Medicamento("Omeprazol 20mg", "Tratamiento para la acidez estomacal y gastritis.", "$11.300", "4.7", false, "💊"),
    Medicamento("Loratadina 10mg", "Antihistamínico para el alivio de alergias y rinitis.", "$6.800", "4.5", true, "🌿")
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

    val medicamentosFiltrados = listaMedicamentos.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) ||
                it.descripcion.contains(searchQuery, ignoreCase = true)
    }

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
                    label = { Text("Carrito") }
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

            items(medicamentosFiltrados) { medicamento ->
                ProductCard(medicamento = medicamento)
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
@Composable
fun ProductCard(medicamento: Medicamento) {
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

                    Button(
                        onClick = { },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Agregar",
                            style = MaterialTheme.typography.labelMedium
                        )
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
