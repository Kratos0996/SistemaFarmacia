package com.example.sistemafarmacia.model

data class Pedido(
    val id: Int = 0,
    val nombreUsuario: String,
    val fecha: String,
    val total: String,
    val resumenProductos: String,
    val direccionEnvio: String
)
