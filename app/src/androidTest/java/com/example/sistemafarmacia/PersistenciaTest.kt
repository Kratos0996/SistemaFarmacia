package com.example.sistemafarmacia

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sistemafarmacia.data.local.FarmaciaDatabase
import com.example.sistemafarmacia.data.repository.FarmaciaRepository
import com.example.sistemafarmacia.model.Medicamento
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.Before
import org.junit.After
import org.junit.runner.RunWith
import java.security.MessageDigest
import java.util.UUID

// Cada prueba usa una base separada de los datos de la aplicación.
@RunWith(AndroidJUnit4::class)
class PersistenciaTest {
    private lateinit var actividad: ActivityScenario<MainActivity>
    // Mantiene el proceso activo durante las pruebas en el teléfono.
    @Before fun abrirActividad() { actividad = ActivityScenario.launch(MainActivity::class.java) }
    @After fun cerrarActividad() { actividad.close() }
    private val contexto = ApplicationProvider.getApplicationContext<Context>()
    private fun abrirBase(nombreBase: String) = Room.databaseBuilder(contexto, FarmaciaDatabase::class.java, nombreBase)
        .addMigrations(FarmaciaDatabase.MIGRACION_1_3, FarmaciaDatabase.MIGRACION_3_4).build()
    private fun medicamentoPrueba(id: Int = 0) = Medicamento(id, "Producto prueba", "Descripción", "$7.000", categoria = "Vitaminas")

    @Test fun catalogoVacioYUsuarioSeConservanAlReabrir() = runBlocking {
        val nombreBase = "test-${UUID.randomUUID()}"
        var baseDatos = abrirBase(nombreBase)
        try {
            var repositorio = FarmaciaRepository(baseDatos.farmaciaDao())
            repositorio.cargarDatosIniciales(listOf(medicamentoPrueba(1)))
            assertTrue(repositorio.autenticarUsuario("", "").isFailure)
            assertTrue(repositorio.autenticarUsuario("admin", "incorrecta").isFailure)
            assertEquals("admin", repositorio.autenticarUsuario(" admin ", "Farmacia123!").getOrThrow().nombreUsuario)
            val hash = baseDatos.farmaciaDao().buscarUsuario("admin")!!.passwordHash
            assertNotEquals("Farmacia123!", hash)
            repositorio.eliminarMedicamento(1)
            baseDatos.close()
            baseDatos = abrirBase(nombreBase)
            repositorio = FarmaciaRepository(baseDatos.farmaciaDao())
            repositorio.cargarDatosIniciales(listOf(medicamentoPrueba(1)))
            assertTrue(repositorio.medicamentos.first().isEmpty())
            assertEquals(hash, baseDatos.farmaciaDao().buscarUsuario("admin")!!.passwordHash)
        } finally { baseDatos.close(); contexto.deleteDatabase(nombreBase) }
    }

    @Test fun medicamentosCarritoYPedidosSeConservan() = runBlocking {
        val nombreBase = "test-${UUID.randomUUID()}"
        var baseDatos = abrirBase(nombreBase)
        try {
            var repositorio = FarmaciaRepository(baseDatos.farmaciaDao())
            repositorio.cargarDatosIniciales(emptyList())
            repositorio.agregarMedicamento(medicamentoPrueba())
            val medicamento = repositorio.medicamentos.first().single()
            repositorio.actualizarMedicamento(medicamento.copy(nombre = "Nombre editado", precio = "$9.000"))
            coroutineScope { repeat(25) { launch { repositorio.aumentarCantidad(medicamento.id) } } }
            assertEquals(25, repositorio.cantidadesCarrito.first()[medicamento.id])
            repositorio.disminuirCantidad(medicamento.id)
            baseDatos.close()
            baseDatos = abrirBase(nombreBase)
            repositorio = FarmaciaRepository(baseDatos.farmaciaDao())
            assertEquals("Nombre editado", repositorio.medicamentos.first().single().nombre)
            assertEquals(24, repositorio.cantidadesCarrito.first()[medicamento.id])
            repositorio.registrarPedido("admin", "Dirección prueba")
            assertTrue(repositorio.cantidadesCarrito.first().isEmpty())
            assertEquals(1, repositorio.observarPedidos("admin").first().size)
            assertEquals("$216.000", repositorio.observarPedidos("admin").first().single().total)
            assertEquals("Nombre editado x24", repositorio.observarPedidos("admin").first().single().resumenProductos)
            repositorio.aumentarCantidad(medicamento.id)
            repositorio.eliminarMedicamento(medicamento.id)
            assertTrue(repositorio.cantidadesCarrito.first().isEmpty())
            baseDatos.close()
            baseDatos = abrirBase(nombreBase)
            assertTrue(FarmaciaRepository(baseDatos.farmaciaDao()).medicamentos.first().isEmpty())
        } finally { baseDatos.close(); contexto.deleteDatabase(nombreBase) }
    }

    @Test fun migracionesConservanLosDatosAnteriores() = runBlocking {
        for (version in listOf(1, 3)) {
            val nombreBase = "migration-${UUID.randomUUID()}"
            crearBaseAnterior(nombreBase, version)
            val baseDatos = abrirBase(nombreBase)
            try {
                val dao = baseDatos.farmaciaDao()
                val repositorio = FarmaciaRepository(dao)
                repositorio.cargarDatosIniciales(listOf(medicamentoPrueba(1)))
                assertEquals("Medicamento legado", repositorio.medicamentos.first().single().nombre)
                assertEquals(mapOf(42 to 3), repositorio.cantidadesCarrito.first())
                if (version == 3) {
                    assertEquals(1, repositorio.observarPedidos("admin").first().size)
                    assertEquals("Dirección conservada", dao.buscarUsuario("admin")!!.direccionEnvio)
                    assertTrue(repositorio.autenticarUsuario("admin", "Farmacia123!").isSuccess)
                    assertTrue(dao.buscarUsuario("admin")!!.passwordHash.length > 64)
                }
                repositorio.eliminarMedicamento(42)
                assertTrue(repositorio.cantidadesCarrito.first().isEmpty())
            } finally { baseDatos.close(); contexto.deleteDatabase(nombreBase) }
        }
    }

    private fun crearBaseAnterior(nombreBase: String, version: Int) {
        val archivo = contexto.getDatabasePath(nombreBase)
        archivo.parentFile!!.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(archivo, null).use { baseDatos ->
            baseDatos.execSQL("CREATE TABLE medicamentos (id INTEGER NOT NULL PRIMARY KEY, nombre TEXT NOT NULL, descripcion TEXT NOT NULL, precio TEXT NOT NULL, calificacion TEXT NOT NULL, enOferta INTEGER NOT NULL, imagenEmoji TEXT NOT NULL, imagenRes INTEGER, categoria TEXT NOT NULL)")
            baseDatos.execSQL("CREATE TABLE cart_items (medicamentoId INTEGER NOT NULL PRIMARY KEY, quantity INTEGER NOT NULL)")
            baseDatos.execSQL("INSERT INTO medicamentos VALUES (42, 'Medicamento legado', 'Descripción', '$7.000', '4.8', 1, '', NULL, 'Vitaminas')")
            baseDatos.execSQL("INSERT INTO cart_items VALUES (42, 3)")
            baseDatos.execSQL("INSERT INTO cart_items VALUES (999, 2)") // Referencia sin medicamento, para comprobar la migración.
            if (version == 3) {
                baseDatos.execSQL("CREATE TABLE users (username TEXT NOT NULL PRIMARY KEY, passwordHash TEXT NOT NULL, salt TEXT NOT NULL, fullName TEXT NOT NULL, email TEXT NOT NULL, role TEXT NOT NULL, direccionEnvio TEXT NOT NULL)")
                baseDatos.execSQL("CREATE TABLE pedidos (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, username TEXT NOT NULL, fechaFormatted TEXT NOT NULL, totalFormatted TEXT NOT NULL, itemsSummary TEXT NOT NULL, direccionEnvio TEXT NOT NULL)")
                val sal = "bGVnYWN5LXNhbHQ="
                val hashAnterior = MessageDigest.getInstance("SHA-256").digest(("Farmacia123!" + sal).toByteArray()).joinToString("") { "%02x".format(it) }
                baseDatos.execSQL("INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?)", arrayOf("admin", hashAnterior, sal, "Admin anterior", "admin@example.test", "Administrador", "Dirección conservada"))
                baseDatos.execSQL("INSERT INTO pedidos VALUES (1, 'admin', '01/01/2026', '$7.000', 'Pedido conservado', 'Dirección conservada')")
            }
            baseDatos.version = version
        }
    }
}
