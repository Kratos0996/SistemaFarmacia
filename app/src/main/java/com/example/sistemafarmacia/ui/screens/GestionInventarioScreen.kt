package com.example.sistemafarmacia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sistemafarmacia.model.Medicamento
import com.example.sistemafarmacia.ui.components.ResumenInventario
import com.example.sistemafarmacia.ui.components.FormularioMedicamento
import com.example.sistemafarmacia.ui.components.TarjetaProducto

@Composable
fun GestionInventarioScreen(
    listaMedicamentos: List<Medicamento>,
    alAgregarMedicamento: (Medicamento) -> Unit,
    alActualizarMedicamento: (Medicamento) -> Unit,
    alEliminarMedicamento: (Int) -> Unit
) {
    var mostrarFormulario by rememberSaveable { mutableStateOf(false) }
    var idEnEdicion by rememberSaveable { mutableStateOf<Int?>(null) }
    var idParaEliminar by rememberSaveable { mutableStateOf<Int?>(null) }
    val medicamentoEnEdicion = listaMedicamentos.find { it.id == idEnEdicion }
    val medicamentoEliminar = listaMedicamentos.find { it.id == idParaEliminar }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    idEnEdicion = null
                    mostrarFormulario = true
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = { Text(text = "Nuevo Producto") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingContenido ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingContenido)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gestión de Inventario",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Administra, registra, edita o elimina productos del catálogo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                ResumenInventario(medicamentos = listaMedicamentos)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Productos en Catálogo (${listaMedicamentos.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (listaMedicamentos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay productos registrados en el inventario",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(listaMedicamentos, key = { it.id }) { medicamento ->
                    TarjetaProducto(
                        medicamento = medicamento,
                        mostrarControles = true,
                        alEditar = {
                            idEnEdicion = medicamento.id
                            mostrarFormulario = true
                        },
                        alEliminar = {
                            idParaEliminar = medicamento.id
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    if (mostrarFormulario) {
        FormularioMedicamento(
            medicamentoEditar = medicamentoEnEdicion,
            alCancelar = { mostrarFormulario = false },
            alGuardar = { medicamentoGuardado ->
                if (medicamentoEnEdicion == null) {
                    alAgregarMedicamento(medicamentoGuardado)
                } else {
                    alActualizarMedicamento(medicamentoGuardado)
                }
                mostrarFormulario = false
            }
        )
    }

    if (medicamentoEliminar != null) {
        AlertDialog(
            onDismissRequest = { idParaEliminar = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Eliminar Producto") },
            text = {
                Text("¿Eliminar '${medicamentoEliminar?.nombre}' del inventario? También se retirará del carrito.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        medicamentoEliminar?.let { alEliminarMedicamento(it.id) }
                        idParaEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { idParaEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
