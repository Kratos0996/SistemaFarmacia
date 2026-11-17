package com.example.sistemafarmacia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Guarda si el catálogo inicial ya se cargó, incluso cuando después queda vacío.
@Entity(tableName = "app_metadata")
data class AppMetadataEntity(
    @PrimaryKey val key: String,
    val value: String
)
