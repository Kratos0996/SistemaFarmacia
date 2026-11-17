package com.example.sistemafarmacia.model

data class Usuario(
    val nombreUsuario: String,
    val nombreCompleto: String,
    val correo: String,
    val rol: String = "Administrador",
    val direccionEnvio: String = "Calle 12 # 34 - 56, Barrio Centro"
)
