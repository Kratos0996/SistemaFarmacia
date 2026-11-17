package com.example.sistemafarmacia

import com.example.sistemafarmacia.util.SeguridadContrasenas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeguridadContrasenasTest {
    @Test
    fun hashCoincideConElValorDeReferencia() {
        // Resultado de referencia calculado con los mismos parámetros de PBKDF2.
        assertEquals(
            "pbkdf2_sha1\$210000\$ed313a873869e046be237dd0233fb68cb06011a89ccdf677e906c9105150767d",
            SeguridadContrasenas.calcularHash("Farmacia123!", "known-salt")
        )
    }

    @Test
    fun contrasenaCorrectaPermiteElAcceso() {
        val sal = SeguridadContrasenas.generarSal()
        val hash = SeguridadContrasenas.calcularHash("Farmacia123!", sal)
        assertTrue(SeguridadContrasenas.verificarContrasena("Farmacia123!", sal, hash))
        assertFalse(SeguridadContrasenas.verificarContrasena("incorrecta", sal, hash))
        assertFalse(SeguridadContrasenas.necesitaActualizar(hash))
    }

    @Test
    fun cadaSalProduceUnHashDistinto() {
        val primeraSal = SeguridadContrasenas.generarSal()
        val segundaSal = SeguridadContrasenas.generarSal()
        assertEquals(32, primeraSal.length)
        assertNotEquals(primeraSal, segundaSal)
        assertNotEquals(SeguridadContrasenas.calcularHash("igual", primeraSal), SeguridadContrasenas.calcularHash("igual", segundaSal))
    }

    @Test
    fun contrasenaAnteriorSePuedeActualizar() {
        val hashAnterior = "df0adf7f133f93da805d0b5c1201ca3bd7e336167b8e3009afa44ba7431265f9"
        assertTrue(SeguridadContrasenas.verificarContrasena("Farmacia123!", "legacy-salt", hashAnterior))
        assertFalse(SeguridadContrasenas.verificarContrasena("incorrecta", "legacy-salt", hashAnterior))
        assertTrue(SeguridadContrasenas.necesitaActualizar(hashAnterior))
    }

    @Test
    fun hashInvalidoRechazaElAcceso() {
        assertFalse(SeguridadContrasenas.verificarContrasena("x", "sal", "pbkdf2_sha1\$invalid\$x"))
        assertFalse(SeguridadContrasenas.verificarContrasena("x", "sal", "pbkdf2_sha1\$999999999\$x"))
    }
}
