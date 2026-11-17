package com.example.sistemafarmacia.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Spa
import androidx.compose.ui.graphics.vector.ImageVector

object IconosCategoria {
    fun porCategoria(nombreCategoria: String?): ImageVector {
        return when (nombreCategoria?.lowercase()?.trim()) {
            "medicamentos" -> Icons.Default.Medication
            "vitaminas" -> Icons.Default.HealthAndSafety
            "cuidado personal" -> Icons.Default.CleanHands
            "bebé y maternidad", "bebe y maternidad" -> Icons.Default.ChildCare
            "dermocosmética", "dermocosmetica" -> Icons.Default.Spa
            else -> Icons.Default.MedicalServices
        }
    }
}
