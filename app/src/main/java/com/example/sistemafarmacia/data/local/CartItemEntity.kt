package com.example.sistemafarmacia.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_items",
    foreignKeys = [ForeignKey(
        entity = MedicamentoEntity::class,
        parentColumns = ["id"],
        childColumns = ["medicamentoId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class CartItemEntity(
    @PrimaryKey val medicamentoId: Int,
    val quantity: Int
)

// Detalle del carrito para calcular el pedido dentro de la transacción.
data class DetalleCarrito(
    val nombre: String,
    val precio: String,
    val cantidad: Int
)
