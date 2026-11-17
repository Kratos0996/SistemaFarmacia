package com.example.sistemafarmacia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sistemafarmacia.data.repository.FarmaciaRepository
import com.example.sistemafarmacia.model.Medicamento
import com.example.sistemafarmacia.model.Pedido
import com.example.sistemafarmacia.model.Usuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class FarmaciaViewModel(private val repositorio: FarmaciaRepository) : ViewModel() {
    // Cada acceso usa su propio historial de navegación.
    var claveSesion: String = UUID.randomUUID().toString()
        private set
    private var tareaInicializacion: Job? = null
    private var errorInicializacion: Exception? = null
    private var tareaAcceso: Job? = null

    private val _errorOperacion = MutableStateFlow<String?>(null)
    val errorOperacion: StateFlow<String?> = _errorOperacion.asStateFlow()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _autenticando = MutableStateFlow(false)
    val autenticando: StateFlow<Boolean> = _autenticando.asStateFlow()

    private val _errorAcceso = MutableStateFlow<String?>(null)
    val errorAcceso: StateFlow<String?> = _errorAcceso.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda = _busqueda.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow<String?>(null)
    val categoriaSeleccionada = _categoriaSeleccionada.asStateFlow()

    val cantidadesCarrito: StateFlow<Map<Int, Int>> = repositorio.cantidadesCarrito.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyMap()
    )

    val listaMedicamentos: StateFlow<List<Medicamento>> = repositorio.medicamentos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val medicamentosFiltrados: StateFlow<List<Medicamento>> = combine(
        listaMedicamentos, _busqueda, _categoriaSeleccionada
    ) { lista, texto, categoria ->
        lista.filter { medicamento ->
            val coincideBusqueda = medicamento.nombre.contains(texto, ignoreCase = true) ||
                medicamento.descripcion.contains(texto, ignoreCase = true)
            val coincideCategoria = categoria == null || medicamento.categoria.equals(categoria, ignoreCase = true)
            coincideBusqueda && coincideCategoria
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val historialPedidos: StateFlow<List<Pedido>> = _usuarioActual
        .flatMapLatest { usuario ->
            if (usuario == null) flowOf(emptyList())
            else repositorio.observarPedidos(usuario.nombreUsuario)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        cargarDatosIniciales()
    }

    private fun cargarDatosIniciales() {
        tareaInicializacion = viewModelScope.launch {
            try {
                repositorio.cargarDatosIniciales()
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                errorInicializacion = error
                _errorAcceso.value = "No se pudo preparar la base de datos. Vuelve a abrir la aplicación."
            }
        }
    }

    fun iniciarSesion(nombreUsuario: String, contrasena: String) {
        if (_autenticando.value) return
        if (nombreUsuario.isBlank() || contrasena.isEmpty()) {
            _errorAcceso.value = "Por favor ingresa el usuario y la contraseña"
            return
        }
        _autenticando.value = true
        _errorAcceso.value = null
        tareaAcceso = viewModelScope.launch {
            try {
                tareaInicializacion?.join()
                check(errorInicializacion == null) { "No se pudo preparar la base de datos. Vuelve a abrir la aplicación." }
                repositorio.autenticarUsuario(nombreUsuario, contrasena)
                    .onSuccess { usuario ->
                        claveSesion = UUID.randomUUID().toString()
                        _usuarioActual.value = usuario
                    }.onFailure { error ->
                        _errorAcceso.value = error.message ?: "Error al iniciar sesión"
                    }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                _errorAcceso.value = error.message ?: "Error al iniciar sesión"
            } finally {
                _autenticando.value = false
            }
        }
    }

    fun cerrarSesion() {
        tareaAcceso?.cancel()
        _usuarioActual.value = null
        _busqueda.value = ""
        _categoriaSeleccionada.value = null
        _errorAcceso.value = null
        _errorOperacion.value = null
    }

    fun limpiarErrorOperacion() {
        _errorOperacion.value = null
    }

    fun actualizarDireccionEnvio(nuevaDireccion: String) {
        val usuario = usuarioActual.value ?: return
        val direccion = nuevaDireccion.trim()
        if (direccion.isBlank()) return
        ejecutarOperacion {
            repositorio.actualizarDireccionEnvio(usuario.nombreUsuario, direccion)
            if (_usuarioActual.value?.nombreUsuario == usuario.nombreUsuario) {
                _usuarioActual.value = usuario.copy(direccionEnvio = direccion)
            }
        }
    }

    fun registrarPedido(alGuardar: () -> Unit) {
        val usuario = usuarioActual.value ?: return
        ejecutarOperacion {
            repositorio.registrarPedido(usuario.nombreUsuario, usuario.direccionEnvio)
            alGuardar()
        }
    }

    fun cambiarBusqueda(texto: String) {
        _busqueda.value = texto
    }

    fun seleccionarCategoria(categoria: String) {
        _categoriaSeleccionada.value = if (_categoriaSeleccionada.value == categoria) null else categoria
    }

    fun aumentarCantidad(medicamentoId: Int) {
        ejecutarOperacion { repositorio.aumentarCantidad(medicamentoId) }
    }

    fun disminuirCantidad(medicamentoId: Int) {
        ejecutarOperacion { repositorio.disminuirCantidad(medicamentoId) }
    }

    fun agregarMedicamento(medicamento: Medicamento) {
        ejecutarOperacion { repositorio.agregarMedicamento(medicamento) }
    }

    fun actualizarMedicamento(medicamento: Medicamento) {
        ejecutarOperacion { repositorio.actualizarMedicamento(medicamento) }
    }

    fun eliminarMedicamento(medicamentoId: Int) {
        ejecutarOperacion { repositorio.eliminarMedicamento(medicamentoId) }
    }

    fun vaciarCarrito() {
        ejecutarOperacion { repositorio.vaciarCarrito() }
    }

    private fun ejecutarOperacion(operacion: suspend () -> Unit) {
        if (_usuarioActual.value == null) return
        viewModelScope.launch {
            try {
                operacion()
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                _errorOperacion.value = error.message ?: "No se pudo completar la operación. Inténtalo de nuevo."
            }
        }
    }
}

class FarmaciaViewModelFactory(private val repositorio: FarmaciaRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(FarmaciaViewModel::class.java))
        return FarmaciaViewModel(repositorio) as T
    }
}
