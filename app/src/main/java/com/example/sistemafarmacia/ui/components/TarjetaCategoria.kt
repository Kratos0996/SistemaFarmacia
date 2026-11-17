package com.example.sistemafarmacia.ui.components

import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sistemafarmacia.model.Categoria
import com.example.sistemafarmacia.ui.util.IconosCategoria

@Composable
fun TarjetaCategoria(
    categoria: Categoria,
    seleccionada: Boolean = false,
    alSeleccionar: () -> Unit = {}
) {
    Card(
        modifier = Modifier.selectable(
            selected = seleccionada,
            role = Role.RadioButton,
            onClick = alSeleccionar
        ),
        colors = if (seleccionada) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = IconosCategoria.porCategoria(categoria.nombre),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (seleccionada) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = categoria.nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Medium,
                color = if (seleccionada) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
