package com.example.sistemafarmacia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val passwordHash: String,
    val salt: String,
    val fullName: String,
    val email: String,
    val role: String = "Administrador",
    val direccionEnvio: String = "Calle 12 # 34 - 56, Barrio Centro"
)
