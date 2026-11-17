package com.example.sistemafarmacia.model

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
