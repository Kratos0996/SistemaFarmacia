package com.example.sistemafarmacia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val fechaFormatted: String,
    val totalFormatted: String,
    val itemsSummary: String,
    val direccionEnvio: String
)
