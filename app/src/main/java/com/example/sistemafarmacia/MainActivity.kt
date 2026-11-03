package com.example.sistemafarmacia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sistemafarmacia.data.local.FarmaciaDatabase
import com.example.sistemafarmacia.data.repository.FarmaciaRepository
import com.example.sistemafarmacia.ui.theme.SistemaFarmaciaTheme
import com.example.sistemafarmacia.viewmodel.FarmaciaViewModel
import com.example.sistemafarmacia.viewmodel.FarmaciaViewModelFactory

data class Medicamento(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: String,
    val calificacion: String = "4.8",
    val enOferta: Boolean = false,
    val imagenEmoji: String = "💊",
    val imagenRes: Int? = null,
    val categoria: String = "Medicamentos"
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

        val dao = FarmaciaDatabase.getDatabase(applicationContext).farmaciaDao()
        val repository = FarmaciaRepository(dao)
        val factory = FarmaciaViewModelFactory(repository)

        setContent {
            SistemaFarmaciaTheme {
                val farmaciaViewModel: FarmaciaViewModel = viewModel(factory = factory)
                PharmacyApp(viewModel = farmaciaViewModel)
            }
        }
    }
}

// ==========================================
// PANTALLA PRINCIPAL
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacyApp(viewModel: FarmaciaViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val cartQuantity by viewModel.cartQuantity.collectAsStateWithLifecycle()
    val listaMedicamentos by viewModel.listaMedicamentos.collectAsStateWithLifecycle()
    val medicamentosFiltrados by viewModel.medicamentosFiltrados.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    val defaultMedicamentos = remember {
        listOf(
            Medicamento(
                id = 1,
                nombre = "Paracetamol 500mg",
                descripcion = "Analgésico y antipirético para aliviar el dolor y la fiebre.",
                precio = "$4.500",
                calificacion = "4.8",
                enOferta = true,
                imagenEmoji = "💊",
                categoria = "Medicamentos"
            ),
            Medicamento(
                id = 2,
                nombre = "Vitamina C 1000mg",
                descripcion = "Suplemento vitamínico para reforzar el sistema inmune.",
                precio = "$8.200",
                calificacion = "4.6",
                enOferta = false,
                imagenEmoji = "🍊",
                categoria = "Vitaminas"
            ),
            Medicamento(
                id = 3,
                nombre = "Alcohol en Gel 250ml",
                descripcion = "Desinfectante para manos de uso diario.",
                precio = "$3.900",
                calificacion = "4.9",
                enOferta = true,
                imagenEmoji = "🩹",
                categoria = "Cuidado Personal"
            ),
            Medicamento(
                id = 4,
                nombre = "Pañales Etapa 1",
                descripcion = "Pañales ultra absorbentes para recién nacidos.",
                precio = "$18.300",
                calificacion = "4.7",
                enOferta = false,
                imagenEmoji = "👶",
                categoria = "Bebé y Maternidad"
            ),
            Medicamento(
                id = 5,
                nombre = "Protector Solar FPS 50",
                descripcion = "Protección solar facial de alta eficacia.",
                precio = "$14.800",
                calificacion = "4.5",
                enOferta = true,
                imagenEmoji = "🧴",
                categoria = "Dermocosmética"
            ),
            Medicamento(
                id = 6,
                nombre = "Ibuprofeno 400mg",
                descripcion = "Antiinflamatorio y analgésico para el alivio de dolores articulares.",
                precio = "$6.200",
                calificacion = "4.7",
                enOferta = false,
                imagenEmoji = "💊",
                categoria = "Medicamentos"
            ),
            Medicamento(
                id = 7,
                nombre = "Multivitamínico Completo",
                descripcion = "Complejo B, zinc y minerales para la energía diaria.",
                precio = "$12.900",
                calificacion = "4.9",
                enOferta = true,
                imagenEmoji = "🍊",
                categoria = "Vitaminas"
            ),
            Medicamento(
                id = 8,
                nombre = "Jabón Neutro Hipoalergénico",
                descripcion = "Limpieza suave para pieles sensibles y delicadas.",
                precio = "$5.400",
                calificacion = "4.6",
                enOferta = false,
                imagenEmoji = "🩹",
                categoria = "Cuidado Personal"
            ),
            Medicamento(
                id = 9,
                nombre = "Fórmula Láctea Maternidad 1",
                descripcion = "Nutrición completa enriquecida con DHA e Hierro para lactantes.",
                precio = "$24.500",
                calificacion = "4.8",
                enOferta = true,
                imagenEmoji = "👶",
                categoria = "Bebé y Maternidad"
            ),
            Medicamento(
                id = 10,
                nombre = "Sérum Ácido Hialurónico",
                descripcion = "Hidratante facial intensivo para prevenir líneas de expresión.",
                precio = "$19.900",
                calificacion = "4.9",
                enOferta = false,
                imagenEmoji = "🧴",
                categoria = "Dermocosmética"
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.initDefaultData(defaultMedicamentos)
    }

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

    val cartCount = cartQuantity.values.sum()

    // Cálculo del valor total del carrito
    val totalCartValue = listaMedicamentos.sumOf { med ->
        val qty = cartQuantity[med.id] ?: 0
        val priceNum = med.precio.filter { it.isDigit() }.toIntOrNull() ?: 0
        qty * priceNum
    }
    val totalFormatted = "$${"%,d".format(totalCartValue).replace(",", ".")}"

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "inicio"

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "GestorPharma",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
                if (currentRoute == "inicio") {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Buscar medicamento...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == "inicio",
                    onClick = {
                        navController.navigate("inicio") {
                            popUpTo("inicio") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Text("🏠") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = currentRoute == "carrito",
                    onClick = {
                        navController.navigate("carrito") {
                            popUpTo("inicio") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Text("🛒") },
                    label = { Text("Carrito ($cartCount)") }
                )
                NavigationBarItem(
                    selected = currentRoute == "perfil",
                    onClick = {
                        navController.navigate("perfil") {
                            popUpTo("inicio") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Text("👤") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "inicio",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("inicio") {
                InicioScreen(
                    listaCategorias = listaCategorias,
                    selectedCategory = selectedCategory,
                    onCategorySelect = { viewModel.onCategorySelect(it) },
                    medicamentosFiltrados = medicamentosFiltrados,
                    cartQuantity = cartQuantity,
                    onIncrease = { viewModel.increaseQuantity(it) },
                    onDecrease = { viewModel.decreaseQuantity(it) }
                )
            }
            composable("carrito") {
                CarritoScreen(
                    cartQuantity = cartQuantity,
                    listaMedicamentos = listaMedicamentos,
                    totalFormatted = totalFormatted,
                    onIncrease = { viewModel.increaseQuantity(it) },
                    onDecrease = { viewModel.decreaseQuantity(it) }
                )
            }
            composable("perfil") {
                PerfilScreen()
            }
        }
    }
}

// ==========================================
// PANTALLA INICIO
// ==========================================
@Composable
fun InicioScreen(
    listaCategorias: List<Categoria>,
    selectedCategory: String?,
    onCategorySelect: (String) -> Unit,
    medicamentosFiltrados: List<Medicamento>,
    cartQuantity: Map<Int, Int>,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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
                        CategoryCard(
                            categoria = cat,
                            isSelected = selectedCategory == cat.nombre,
                            onClick = { onCategorySelect(cat.nombre) }
                        )
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
                    onIncrease = { onIncrease(medicamento.id) },
                    onDecrease = { onDecrease(medicamento.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==========================================
// PANTALLA CARRITO
// ==========================================
@Composable
fun CarritoScreen(
    cartQuantity: Map<Int, Int>,
    listaMedicamentos: List<Medicamento>,
    totalFormatted: String,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit
) {
    val itemsEnCarrito = listaMedicamentos.filter { (cartQuantity[it.id] ?: 0) > 0 }

    if (itemsEnCarrito.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tu carrito está vacío 🛒",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tu Carrito de Compras",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(itemsEnCarrito, key = { it.id }) { medicamento ->
                val quantity = cartQuantity[medicamento.id] ?: 0
                ProductCard(
                    medicamento = medicamento,
                    quantity = quantity,
                    onIncrease = { onIncrease(medicamento.id) },
                    onDecrease = { onDecrease(medicamento.id) }
                )
            }

            // TOTAL DEL CARRITO
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total a pagar:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = totalFormatted,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// PANTALLA PERFIL
// ==========================================
@Composable
fun PerfilScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "👤",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Usuario",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

// ==========================================
// CARD PARA CATEGORÍA
// ==========================================
@Composable
fun CategoryCard(
    categoria: Categoria,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = categoria.emoji)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = categoria.nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ==========================================
// CARD PARA MEDICAMENTO
// ==========================================
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
