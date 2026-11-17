package com.example.sistemafarmacia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sistemafarmacia.model.Medicamento
import com.example.sistemafarmacia.ui.util.IconosCategoria

@Composable
fun TarjetaProducto(
    medicamento: Medicamento,
    cantidad: Int = 0,
    mostrarControles: Boolean = false,
    alAumentar: () -> Unit = {},
    alDisminuir: () -> Unit = {},
    alEditar: () -> Unit = {},
    alEliminar: () -> Unit = {}
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = IconosCategoria.porCategoria(medicamento.categoria),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medicamento.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = medicamento.categoria,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.semantics(mergeDescendants = true) {
                                contentDescription = "Calificación: ${medicamento.calificacion} de 5"
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(medicamento.calificacion, style = MaterialTheme.typography.labelSmall)
                        }
                        if (medicamento.enOferta) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalOffer,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Oferta",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (medicamento.descripcion.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = medicamento.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = medicamento.precio,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (mostrarControles) {
                    IconButton(onClick = alEditar, modifier = Modifier.size(48.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar ${medicamento.nombre}",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = alEliminar, modifier = Modifier.size(48.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar ${medicamento.nombre}",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                } else if (cantidad <= 0) {
                    Button(onClick = alAumentar, modifier = Modifier.heightIn(min = 48.dp).semantics {
                        contentDescription = "Agregar ${medicamento.nombre}"
                    }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar")
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedIconButton(onClick = alDisminuir, modifier = Modifier.size(48.dp)) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Disminuir cantidad de ${medicamento.nombre}",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = cantidad.toString(),
                            modifier = Modifier.padding(horizontal = 12.dp).semantics {
                                contentDescription = "Cantidad: $cantidad"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedIconButton(onClick = alAumentar, modifier = Modifier.size(48.dp)) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Aumentar cantidad de ${medicamento.nombre}",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
