package com.example.sistemafarmacia

import android.content.Intent
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sistemafarmacia.data.local.FarmaciaDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlujoAplicacionTest {
    @get:Rule val interfaz = createAndroidComposeRule<MainActivity>()
    private var nombreProductoCreado: String? = null
    @After fun retirarProductoDePrueba() {
        runBlocking {
            val nombre = nombreProductoCreado ?: return@runBlocking
            val contexto = InstrumentationRegistry.getInstrumentation().targetContext
            val dao = FarmaciaDatabase.obtenerInstancia(contexto).farmaciaDao()
            dao.observarMedicamentos().first().find { it.nombre == nombre }?.let { dao.eliminarMedicamento(it.id) }
        }
    }
    private fun listaVertical() = interfaz.onNode(
        hasScrollToIndexAction() and SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange)
    )

    private fun esperarTexto(texto: String) {
        interfaz.waitUntil(20_000) { interfaz.onAllNodesWithText(texto, ignoreCase = true).fetchSemanticsNodes().isNotEmpty() }
    }
    private fun iniciarSesion() {
        esperarTexto("Iniciar sesión")
        interfaz.onNode(hasSetTextAction() and hasText("Usuario")).performTextReplacement("admin")
        interfaz.onNode(hasSetTextAction() and hasText("Contraseña")).performTextReplacement("Farmacia123!")
        Espresso.closeSoftKeyboard()
        interfaz.onNodeWithText("Iniciar sesión", ignoreCase = true).performScrollTo().performClick()
        esperarTexto("Inicio")
    }

    @Test fun accesoRotacionYCierreDeSesion() {
        esperarTexto("Iniciar sesión")
        interfaz.onNodeWithText("Inventario").assertDoesNotExist()
        interfaz.onNodeWithText("Iniciar sesión", ignoreCase = true).performScrollTo().performClick()
        esperarTexto("Por favor ingresa el usuario y la contraseña")
        interfaz.onNode(hasSetTextAction() and hasText("Usuario")).performTextReplacement("admin")
        val contrasena = interfaz.onNode(hasSetTextAction() and hasText("Contraseña"))
        contrasena.performTextReplacement("incorrecta")
        contrasena.assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
        interfaz.onNodeWithContentDescription("Mostrar contraseña").performClick()
        contrasena.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { accion ->
            val resultados = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
            accion(resultados)
            org.junit.Assert.assertEquals("incorrecta", resultados.single().layoutInput.text.text)
        }
        interfaz.onNodeWithContentDescription("Ocultar contraseña").performClick()
        Espresso.closeSoftKeyboard()
        interfaz.onNodeWithText("Iniciar sesión", ignoreCase = true).performScrollTo().performClick()
        esperarTexto("Usuario o contraseña incorrectos")
        contrasena.performTextReplacement("Farmacia123!")
        Espresso.closeSoftKeyboard()
        interfaz.onNodeWithText("Iniciar sesión", ignoreCase = true).performScrollTo().performClick()
        esperarTexto("Inicio")
        interfaz.onNodeWithText("Inventario").performClick()
        esperarTexto("Gestión de Inventario")
        interfaz.activityRule.scenario.recreate()
        esperarTexto("Gestión de Inventario")
        interfaz.onNodeWithText("Iniciar sesión", ignoreCase = true).assertDoesNotExist()
        interfaz.onNodeWithText("Perfil").performClick()
        esperarTexto("Administrador GestorPharma")
        interfaz.onNodeWithText("Cerrar Sesión", ignoreCase = true).performScrollTo().performClick()
        esperarTexto("Iniciar sesión")
        interfaz.onNodeWithText("Inventario").assertDoesNotExist()
        val intentoReabrir = Intent(interfaz.activity, MainActivity::class.java)
        Espresso.pressBackUnconditionally()
        ActivityScenario.launch<MainActivity>(intentoReabrir).use {
            esperarTexto("Iniciar sesión")
            interfaz.onNodeWithText("Inventario").assertDoesNotExist()
        }
    }

    @Test fun registroEdicionBusquedaCarritoYEliminacion() {
        iniciarSesion()
        val nombre = "Prueba UI ${System.currentTimeMillis()}"
        nombreProductoCreado = nombre
        interfaz.onNodeWithText("Inventario").performClick()
        esperarTexto("Gestión de Inventario")
        interfaz.onNodeWithText("Nuevo Producto", useUnmergedTree = true).performClick()
        interfaz.onNodeWithText("Guardar").performClick()
        esperarTexto("El nombre del producto es obligatorio")
        interfaz.onNode(hasSetTextAction() and hasText("Nombre del Producto")).performTextInput(nombre)
        interfaz.onNode(hasSetTextAction() and hasText("Precio (sin puntos ni símbolo)")).performTextInput("0")
        Espresso.closeSoftKeyboard()
        interfaz.onNodeWithText("Guardar").performClick()
        esperarTexto("El precio debe ser un número entero positivo")
        interfaz.onNode(hasSetTextAction() and hasText("Precio (sin puntos ni símbolo)")).performTextReplacement("7000")
        Espresso.closeSoftKeyboard()
        interfaz.onNodeWithText("Guardar").performClick()
        listaVertical().performScrollToNode(hasText(nombre))
        interfaz.onNodeWithContentDescription("Editar $nombre").performClick()
        interfaz.onNode(hasSetTextAction() and hasText("Precio (sin puntos ni símbolo)")).performTextReplacement("9000")
        Espresso.closeSoftKeyboard()
        interfaz.onNodeWithText("Guardar").performClick()
        interfaz.onNodeWithText("Inicio").performClick()
        interfaz.onNode(hasSetTextAction()).performTextInput(nombre)
        Espresso.closeSoftKeyboard()
        interfaz.onNode(hasText("Vitaminas") and hasClickAction()).performClick()
        esperarTexto("No se encontraron productos disponibles")
        interfaz.onNode(hasText("Vitaminas") and hasClickAction()).performClick()
        interfaz.onNode(hasText("Medicamentos") and hasClickAction()).performClick()
        listaVertical().performScrollToNode(hasText(nombre))
        interfaz.onNode(hasText(nombre) and !hasSetTextAction()).assertExists()
        interfaz.onNodeWithContentDescription("Agregar $nombre").performClick()
        interfaz.onNodeWithText("Carrito").performClick()
        listaVertical().performScrollToNode(hasText(nombre))
        interfaz.onNodeWithText(nombre).assertExists()
        interfaz.onNodeWithContentDescription("Aumentar cantidad de $nombre").performClick()
        interfaz.onNodeWithContentDescription("Disminuir cantidad de $nombre").performClick()
        interfaz.onNodeWithText("Inventario").performClick()
        listaVertical().performScrollToNode(hasText(nombre))
        interfaz.onNodeWithContentDescription("Eliminar $nombre").performClick()
        interfaz.onNodeWithText("Eliminar").performClick()
        interfaz.onNodeWithText("Carrito").performClick()
        interfaz.onNodeWithText(nombre).assertDoesNotExist()
    }
}
