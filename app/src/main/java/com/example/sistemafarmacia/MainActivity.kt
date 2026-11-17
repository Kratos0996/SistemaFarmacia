package com.example.sistemafarmacia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sistemafarmacia.data.local.FarmaciaDatabase
import com.example.sistemafarmacia.data.repository.FarmaciaRepository
import com.example.sistemafarmacia.model.Categoria
import com.example.sistemafarmacia.model.Categorias
import com.example.sistemafarmacia.model.Rutas
import com.example.sistemafarmacia.ui.screens.CarritoScreen
import com.example.sistemafarmacia.ui.screens.GestionInventarioScreen
import com.example.sistemafarmacia.ui.screens.InicioScreen
import com.example.sistemafarmacia.ui.screens.InicioSesionScreen
import com.example.sistemafarmacia.ui.screens.PerfilScreen
import com.example.sistemafarmacia.ui.theme.SistemaFarmaciaTheme
import com.example.sistemafarmacia.viewmodel.FarmaciaViewModel
import com.example.sistemafarmacia.viewmodel.FarmaciaViewModelFactory
import java.util.Locale
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val baseDatos = FarmaciaDatabase.obtenerInstancia(applicationContext)
        val repositorio = FarmaciaRepository(baseDatos.farmaciaDao())
        val fabrica = FarmaciaViewModelFactory(repositorio)

        setContent {
            SistemaFarmaciaTheme {
                val farmaciaViewModel: FarmaciaViewModel = viewModel(factory = fabrica)
                AppFarmacia(farmaciaViewModel)
            }
        }
    }
}

@Composable
fun AppFarmacia(viewModel: FarmaciaViewModel) {
    val usuarioActual by viewModel.usuarioActual.collectAsStateWithLifecycle()
    val autenticando by viewModel.autenticando.collectAsStateWithLifecycle()
    val errorAcceso by viewModel.errorAcceso.collectAsStateWithLifecycle()

    if (usuarioActual == null) {
        Scaffold { espacio ->
            Box(Modifier.padding(espacio)) {
                InicioSesionScreen(
                    autenticando = autenticando,
                    mensajeError = errorAcceso,
                    alIniciarSesion = { usuario, contrasena -> viewModel.iniciarSesion(usuario, contrasena) }
                )
            }
        }
    } else {
        // Cada sesión tiene su propia navegación; la anterior se descarta al salir.
        key(viewModel.claveSesion) {
            ContenidoFarmacia(viewModel)
        }
    }
}

private data class Destino(val ruta: String, val nombre: String, val icono: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContenidoFarmacia(viewModel: FarmaciaViewModel) {
    val usuarioActual by viewModel.usuarioActual.collectAsStateWithLifecycle()
    val busqueda by viewModel.busqueda.collectAsStateWithLifecycle()
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsStateWithLifecycle()
    val cantidadesCarrito by viewModel.cantidadesCarrito.collectAsStateWithLifecycle()
    val medicamentos by viewModel.listaMedicamentos.collectAsStateWithLifecycle()
    val medicamentosFiltrados by viewModel.medicamentosFiltrados.collectAsStateWithLifecycle()
    val historialPedidos by viewModel.historialPedidos.collectAsStateWithLifecycle()
    val errorOperacion by viewModel.errorOperacion.collectAsStateWithLifecycle()
    val avisos = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    val navegacion = rememberNavController()
    val entradaActual by navegacion.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route ?: Rutas.INICIO

    LaunchedEffect(errorOperacion) {
        errorOperacion?.let { mensaje ->
            avisos.showSnackbar(mensaje)
            viewModel.limpiarErrorOperacion()
        }
    }

    val categorias = Categorias.disponibles.map { Categoria(it) }
    val cantidadCarrito = cantidadesCarrito.values.sum()
    val totalCarrito = medicamentos.sumOf { medicamento ->
        val cantidad = cantidadesCarrito[medicamento.id] ?: 0
        val precio = medicamento.precio.filter(Char::isDigit).toLongOrNull() ?: 0L
        cantidad * precio
    }
    val totalFormateado = "$" + String.format(Locale.US, "%,d", totalCarrito).replace(',', '.')
    val destinos = listOf(
        Destino(Rutas.INICIO, "Inicio", Icons.Default.Home),
        Destino(Rutas.INVENTARIO, "Inventario", Icons.Default.Inventory2),
        Destino(Rutas.CARRITO, "Carrito", Icons.Default.ShoppingCart),
        Destino(Rutas.PERFIL, "Perfil", Icons.Default.Person)
    )

    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = { Text("GestorPharma", fontWeight = FontWeight.Bold) })
                if (rutaActual == Rutas.INICIO) {
                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = { viewModel.cambiarBusqueda(it) },
                        placeholder = { Text("Buscar medicamento...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (busqueda.isNotEmpty()) {
                                IconButton(onClick = { viewModel.cambiarBusqueda("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Limpiar búsqueda")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 8.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar {
                destinos.forEach { destino ->
                    NavigationBarItem(
                        selected = rutaActual == destino.ruta,
                        onClick = {
                            navegacion.navigate(destino.ruta) {
                                popUpTo(Rutas.INICIO) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            BadgedBox(badge = {
                                if (destino.ruta == Rutas.CARRITO && cantidadCarrito > 0) {
                                    Badge {
                                        Text(if (cantidadCarrito > 99) "99+" else cantidadCarrito.toString())
                                    }
                                }
                            }) {
                                Icon(destino.icono, contentDescription = null)
                            }
                        },
                        label = { Text(destino.nombre) }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(avisos) }
    ) { espacio ->
        NavHost(navegacion, startDestination = Rutas.INICIO, modifier = Modifier.padding(espacio)) {
            composable(Rutas.INICIO) {
                InicioScreen(
                    usuarioActual = usuarioActual,
                    listaCategorias = categorias,
                    categoriaSeleccionada = categoriaSeleccionada,
                    alSeleccionarCategoria = { viewModel.seleccionarCategoria(it) },
                    medicamentosFiltrados = medicamentosFiltrados,
                    cantidadesCarrito = cantidadesCarrito,
                    alAumentar = { viewModel.aumentarCantidad(it) },
                    alDisminuir = { viewModel.disminuirCantidad(it) }
                )
            }
            composable(Rutas.INVENTARIO) {
                GestionInventarioScreen(
                    listaMedicamentos = medicamentos,
                    alAgregarMedicamento = { viewModel.agregarMedicamento(it) },
                    alActualizarMedicamento = { viewModel.actualizarMedicamento(it) },
                    alEliminarMedicamento = { viewModel.eliminarMedicamento(it) }
                )
            }
            composable(Rutas.CARRITO) {
                CarritoScreen(
                    cantidadesCarrito = cantidadesCarrito,
                    listaMedicamentos = medicamentos,
                    totalFormateado = totalFormateado,
                    direccionEnvio = usuarioActual?.direccionEnvio.orEmpty(),
                    alAumentar = { viewModel.aumentarCantidad(it) },
                    alDisminuir = { viewModel.disminuirCantidad(it) },
                    alVaciarCarrito = { viewModel.vaciarCarrito() },
                    alConfirmarPedido = {
                        viewModel.registrarPedido {
                            alcance.launch { avisos.showSnackbar("Pedido registrado") }
                        }
                    }
                )
            }
            composable(Rutas.PERFIL) {
                PerfilScreen(
                    usuarioActual = usuarioActual,
                    historialPedidos = historialPedidos,
                    alActualizarDireccion = { viewModel.actualizarDireccionEnvio(it) },
                    alCerrarSesion = { viewModel.cerrarSesion() }
                )
            }
        }
    }
}
