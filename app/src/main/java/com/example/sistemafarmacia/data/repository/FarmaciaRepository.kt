package com.example.sistemafarmacia.data.repository

import com.example.sistemafarmacia.data.DatosIniciales
import com.example.sistemafarmacia.data.local.FarmaciaDao
import com.example.sistemafarmacia.data.local.MedicamentoEntity
import com.example.sistemafarmacia.data.local.PedidoEntity
import com.example.sistemafarmacia.data.local.UserEntity
import com.example.sistemafarmacia.model.Categorias
import com.example.sistemafarmacia.model.Medicamento
import com.example.sistemafarmacia.model.Pedido
import com.example.sistemafarmacia.model.Usuario
import com.example.sistemafarmacia.util.SeguridadContrasenas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FarmaciaRepository(private val dao: FarmaciaDao) {
    val medicamentos: Flow<List<Medicamento>> = dao.observarMedicamentos().map { lista ->
        lista.map { it.aModelo() }
    }

    val cantidadesCarrito: Flow<Map<Int, Int>> = dao.observarCarrito().map { items ->
        items.associate { it.medicamentoId to it.quantity }
    }

    fun observarPedidos(nombreUsuario: String): Flow<List<Pedido>> {
        return dao.observarPedidosUsuario(nombreUsuario).map { lista -> lista.map { it.aModelo() } }
    }

    suspend fun cargarDatosIniciales(medicamentosIniciales: List<Medicamento> = DatosIniciales.medicamentos) = withContext(Dispatchers.IO) {
        dao.cargarMedicamentosIniciales(medicamentosIniciales.map { it.aEntidad() })
        if (dao.buscarUsuario("admin") == null) {
            val sal = SeguridadContrasenas.generarSal()
            val hash = SeguridadContrasenas.calcularHash("Farmacia123!", sal)
            dao.insertarUsuario(
                UserEntity(
                    username = "admin",
                    passwordHash = hash,
                    salt = sal,
                    fullName = "Administrador GestorPharma",
                    email = "admin@gestorpharma.com",
                    role = "Administrador",
                    direccionEnvio = "Calle 12 # 34 - 56, Barrio Centro"
                )
            )
        }
    }

    suspend fun autenticarUsuario(nombreUsuario: String, contrasena: String): Result<Usuario> = withContext(Dispatchers.IO) {
        val nombre = nombreUsuario.trim()
        if (nombre.isEmpty() || contrasena.isEmpty()) {
            return@withContext Result.failure(Exception("Por favor ingresa el usuario y la contraseña"))
        }
        val usuario = dao.buscarUsuario(nombre)
            ?: return@withContext Result.failure(Exception("Usuario o contraseña incorrectos"))

        if (!SeguridadContrasenas.verificarContrasena(contrasena, usuario.salt, usuario.passwordHash)) {
            return@withContext Result.failure(Exception("Usuario o contraseña incorrectos"))
        }
        if (SeguridadContrasenas.necesitaActualizar(usuario.passwordHash)) {
            val sal = SeguridadContrasenas.generarSal()
            dao.actualizarClave(nombre, SeguridadContrasenas.calcularHash(contrasena, sal), sal)
        }
        Result.success(
            Usuario(
                nombreUsuario = usuario.username,
                nombreCompleto = usuario.fullName,
                correo = usuario.email,
                rol = usuario.role,
                direccionEnvio = usuario.direccionEnvio
            )
        )
    }

    suspend fun actualizarDireccionEnvio(nombreUsuario: String, nuevaDireccion: String) {
        require(nuevaDireccion.isNotBlank()) { "La dirección de envío es obligatoria" }
        dao.actualizarDireccionEnvio(nombreUsuario, nuevaDireccion.trim())
    }

    suspend fun registrarPedido(nombreUsuario: String, direccionEnvio: String) {
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        dao.confirmarPedido(nombreUsuario, fecha, direccionEnvio)
    }

    suspend fun agregarMedicamento(medicamento: Medicamento) {
        dao.insertarNuevoMedicamento(validarMedicamento(medicamento).aEntidad())
    }

    suspend fun actualizarMedicamento(medicamento: Medicamento) {
        val medicamentoValidado = validarMedicamento(medicamento)
        requireNotNull(dao.buscarMedicamento(medicamentoValidado.id)) { "El medicamento ya no está disponible" }
        dao.actualizarMedicamento(medicamentoValidado.aEntidad())
    }

    suspend fun eliminarMedicamento(medicamentoId: Int) {
        dao.eliminarMedicamento(medicamentoId)
    }

    suspend fun vaciarCarrito() {
        dao.vaciarCarrito()
    }

    suspend fun aumentarCantidad(medicamentoId: Int) {
        dao.aumentarCantidadCarrito(medicamentoId)
    }

    suspend fun disminuirCantidad(medicamentoId: Int) {
        dao.disminuirCantidadCarrito(medicamentoId)
    }
}

private fun validarMedicamento(medicamento: Medicamento): Medicamento {
    require(medicamento.nombre.isNotBlank()) { "El nombre del producto es obligatorio" }
    require(medicamento.categoria in Categorias.disponibles) { "Selecciona una categoría válida" }
    val precio = medicamento.precio.trim().removePrefix("$")
    require(precio.matches(Regex("(?:[0-9]+|[0-9]{1,3}(?:\\.[0-9]{3})+)"))) {
        "El precio debe ser un número entero positivo"
    }
    val precioNumerico = precio.replace(".", "").toIntOrNull()
    require(precioNumerico != null && precioNumerico > 0) { "El precio debe ser un número entero positivo dentro del rango permitido" }
    return medicamento.copy(nombre = medicamento.nombre.trim(), descripcion = medicamento.descripcion.trim())
}

private fun MedicamentoEntity.aModelo() = Medicamento(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    calificacion = calificacion,
    enOferta = enOferta,
    imagenEmoji = imagenEmoji,
    imagenRes = imagenRes,
    categoria = categoria
)

private fun Medicamento.aEntidad() = MedicamentoEntity(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    calificacion = calificacion,
    enOferta = enOferta,
    imagenEmoji = imagenEmoji,
    imagenRes = imagenRes,
    categoria = categoria
)

private fun PedidoEntity.aModelo() = Pedido(
    id = id,
    nombreUsuario = username,
    fecha = fechaFormatted,
    total = totalFormatted,
    resumenProductos = itemsSummary,
    direccionEnvio = direccionEnvio
)
