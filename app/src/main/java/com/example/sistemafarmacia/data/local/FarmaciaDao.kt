package com.example.sistemafarmacia.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Locale

@Dao
interface FarmaciaDao {
    @Query("SELECT * FROM medicamentos ORDER BY id ASC")
    fun observarMedicamentos(): Flow<List<MedicamentoEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarMedicamentos(medicamentos: List<MedicamentoEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarMedicamento(medicamento: MedicamentoEntity)

    @Query("SELECT COUNT(*) FROM medicamentos")
    suspend fun contarMedicamentos(): Int

    @Query("SELECT COALESCE(MAX(id), 0) FROM medicamentos")
    suspend fun obtenerUltimoIdMedicamento(): Int

    @Query("SELECT * FROM medicamentos WHERE id = :id LIMIT 1")
    suspend fun buscarMedicamento(id: Int): MedicamentoEntity?

    @Transaction
    suspend fun insertarNuevoMedicamento(medicamento: MedicamentoEntity) {
        val ultimoId = obtenerUltimoIdMedicamento()
        check(ultimoId < Int.MAX_VALUE) { "Se alcanzó el límite de identificadores de medicamentos" }
        insertarMedicamento(medicamento.copy(id = ultimoId + 1))
    }

    @Query("SELECT value FROM app_metadata WHERE `key` = :clave LIMIT 1")
    suspend fun obtenerMetadato(clave: String): String?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarMetadato(metadato: AppMetadataEntity)

    @Transaction
    suspend fun cargarMedicamentosIniciales(medicamentos: List<MedicamentoEntity>) {
        if (obtenerMetadato("initial_data_seeded") == null) {
            if (contarMedicamentos() == 0) insertarMedicamentos(medicamentos)
            insertarMetadato(AppMetadataEntity("initial_data_seeded", "true"))
        }
    }

    @Update
    suspend fun actualizarMedicamento(medicamento: MedicamentoEntity)

    @Query("DELETE FROM medicamentos WHERE id = :id")
    suspend fun eliminarMedicamento(id: Int)

    @Query("SELECT * FROM cart_items")
    fun observarCarrito(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarItemCarrito(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE medicamentoId = :medicamentoId")
    suspend fun eliminarItemCarrito(medicamentoId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun vaciarCarrito()

    @Query("SELECT quantity FROM cart_items WHERE medicamentoId = :medicamentoId LIMIT 1")
    suspend fun obtenerCantidad(medicamentoId: Int): Int?

    @Query("""
        SELECT m.nombre, m.precio, c.quantity AS cantidad
        FROM cart_items c INNER JOIN medicamentos m ON m.id = c.medicamentoId
        ORDER BY m.id
    """)
    suspend fun consultarDetalleCarrito(): List<DetalleCarrito>

    @Transaction
    suspend fun aumentarCantidadCarrito(medicamentoId: Int) {
        requireNotNull(buscarMedicamento(medicamentoId)) { "El medicamento ya no está disponible" }
        val cantidadActual = obtenerCantidad(medicamentoId) ?: 0
        require(cantidadActual < 999) { "El máximo por medicamento es de 999 unidades" }
        guardarItemCarrito(CartItemEntity(medicamentoId, cantidadActual + 1))
    }

    @Transaction
    suspend fun disminuirCantidadCarrito(medicamentoId: Int) {
        val cantidadActual = obtenerCantidad(medicamentoId) ?: return
        if (cantidadActual > 1) guardarItemCarrito(CartItemEntity(medicamentoId, cantidadActual - 1))
        else eliminarItemCarrito(medicamentoId)
    }

    @Query("SELECT * FROM users WHERE username = :nombreUsuario LIMIT 1")
    suspend fun buscarUsuario(nombreUsuario: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarUsuario(usuario: UserEntity)

    @Query("UPDATE users SET passwordHash = :hash, salt = :sal WHERE username = :nombreUsuario")
    suspend fun actualizarClave(nombreUsuario: String, hash: String, sal: String)

    @Query("UPDATE users SET direccionEnvio = :nuevaDireccion WHERE username = :nombreUsuario")
    suspend fun actualizarDireccionEnvio(nombreUsuario: String, nuevaDireccion: String)

    @Query("SELECT * FROM pedidos WHERE username = :nombreUsuario ORDER BY id DESC")
    fun observarPedidosUsuario(nombreUsuario: String): Flow<List<PedidoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPedido(pedido: PedidoEntity)

    @Transaction
    suspend fun confirmarPedido(nombreUsuario: String, fecha: String, direccionEnvio: String) {
        // El pedido y el vaciado usan el mismo carrito, aunque la pantalla aún no se haya actualizado.
        val detalle = consultarDetalleCarrito()
        require(detalle.isNotEmpty()) { "Agrega medicamentos al carrito antes de confirmar" }
        val total = detalle.fold(0L) { acumulado, item ->
            val precio = item.precio.filter { it.isDigit() }.toLongOrNull()
            require(precio != null && precio > 0 && item.cantidad > 0) {
                "Revisa los precios y las cantidades del carrito antes de confirmar"
            }
            Math.addExact(acumulado, Math.multiplyExact(precio, item.cantidad.toLong()))
        }
        val resumen = detalle.joinToString("\n") { "${it.nombre} x${it.cantidad}" }
        val totalFormateado = "$" + String.format(Locale.US, "%,d", total).replace(',', '.')
        insertarPedido(
            PedidoEntity(
                username = nombreUsuario,
                fechaFormatted = fecha,
                totalFormatted = totalFormateado,
                itemsSummary = resumen,
                direccionEnvio = direccionEnvio
            )
        )
        vaciarCarrito()
    }
}
