package com.example.sistemafarmacia.util

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object SeguridadContrasenas {
    private const val ITERACIONES = 210_000
    private const val PREFIJO = "pbkdf2_sha1"

    fun generarSal(): String = ByteArray(16).also { SecureRandom().nextBytes(it) }.aHexadecimal()

    fun calcularHash(contrasena: String, sal: String): String {
        val hash = derivarClave(contrasena, sal, ITERACIONES)
        return "$PREFIJO\$$ITERACIONES\$${hash.aHexadecimal()}"
    }

    fun necesitaActualizar(hash: String): Boolean = !hash.startsWith("$PREFIJO\$$ITERACIONES\$")

    fun verificarContrasena(contrasena: String, sal: String, hashGuardado: String): Boolean {
        if (!hashGuardado.startsWith("$PREFIJO\$")) {
            // Al ingresar se actualizan las claves de las instalaciones anteriores.
            val hashAnterior = MessageDigest.getInstance("SHA-256")
                .digest((contrasena + sal).toByteArray(Charsets.UTF_8)).aHexadecimal()
            return MessageDigest.isEqual(hashAnterior.toByteArray(), hashGuardado.toByteArray())
        }
        val partes = hashGuardado.split('$')
        val iteraciones = partes.getOrNull(1)?.toIntOrNull() ?: return false
        if (partes.size != 3 || iteraciones !in 10_000..1_000_000 || partes[2].length != 64) return false
        val hashCalculado = derivarClave(contrasena, sal, iteraciones).aHexadecimal()
        return MessageDigest.isEqual(hashCalculado.toByteArray(), partes[2].toByteArray())
    }

    private fun derivarClave(contrasena: String, sal: String, iteraciones: Int): ByteArray {
        val especificacion = PBEKeySpec(contrasena.toCharArray(), sal.toByteArray(Charsets.UTF_8), iteraciones, 256)
        return try {
            // Esta variante de PBKDF2 también está disponible en Android 7.
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(especificacion).encoded
        } finally {
            especificacion.clearPassword()
        }
    }

    private fun ByteArray.aHexadecimal(): String = joinToString("") { "%02x".format(it.toInt() and 0xff) }
}
