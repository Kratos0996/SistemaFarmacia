package com.example.sistemafarmacia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val medicamentoId: Int,
    val quantity: Int
)
