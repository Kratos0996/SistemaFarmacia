package com.example.sistemafarmacia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.sistemafarmacia.model.Medicamento
import com.example.sistemafarmacia.model.Categorias
import com.example.sistemafarmacia.ui.util.IconosCategoria

@Composable
fun FormularioMedicamento(
    medicamentoEditar: Medicamento? = null,
    alCancelar: () -> Unit,
    alGuardar: (Medicamento) -> Unit
) {
    var nombre by rememberSaveable(medicamentoEditar?.id) { mutableStateOf(medicamentoEditar?.nombre ?: "") }
    var descripcion by rememberSaveable(medicamentoEditar?.id) { mutableStateOf(medicamentoEditar?.descripcion ?: "") }
    var precio by rememberSaveable(medicamentoEditar?.id) { mutableStateOf(medicamentoEditar?.precio?.filter(Char::isDigit) ?: "") }
    var categoria by rememberSaveable(medicamentoEditar?.id) { mutableStateOf(medicamentoEditar?.categoria ?: "Medicamentos") }
    var enOferta by rememberSaveable(medicamentoEditar?.id) { mutableStateOf(medicamentoEditar?.enOferta ?: false) }

    var mensajeError by remember { mutableStateOf<String?>(null) }

    val categoriasDisponibles = Categorias.disponibles

    AlertDialog(
        onDismissRequest = alCancelar,
        title = {
            Text(
                text = if (medicamentoEditar == null) "Registrar Medicamento" else "Editar Medicamento",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!mensajeError.isNullOrBlank()) {
                    Text(
                        text = mensajeError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        mensajeError = null
                    },
                    label = { Text("Nombre del Producto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = precio,
                    onValueChange = {
                        precio = it
                        mensajeError = null
                    },
                    label = { Text("Precio (sin puntos ni símbolo)") },
                    placeholder = { Text("Ej: 5000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.size(4.dp))

                Text(
                    text = "Categoría del Producto:",
                    style = MaterialTheme.typography.titleSmall
                )

                Column(modifier = Modifier.selectableGroup()) {
                    categoriasDisponibles.forEach { cat ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).selectable(
                                selected = categoria == cat,
                                role = Role.RadioButton,
                                onClick = { categoria = cat; mensajeError = null }
                            ).padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = (categoria == cat),
                                onClick = null
                            )
                            Icon(
                                imageVector = IconosCategoria.porCategoria(cat),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = cat, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).toggleable(
                        value = enOferta,
                        role = Role.Checkbox,
                        onValueChange = { enOferta = it }
                    )
                ) {
                    Checkbox(
                        checked = enOferta,
                        onCheckedChange = null
                    )
                    Text(text = "Marcar como Oferta", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val nombreLimpio = nombre.trim()
                    val precioIngresado = precio.trim()
                    val precioNumero = precioIngresado.toIntOrNull()

                    if (nombreLimpio.isBlank()) {
                        mensajeError = "El nombre del producto es obligatorio"
                        return@Button
                    }

                    if (precioNumero == null || precioNumero <= 0) {
                        mensajeError = "El precio debe ser un número entero positivo"
                        return@Button
                    }

                    if (categoria !in categoriasDisponibles) {
                        mensajeError = "Selecciona una categoría válida"
                        return@Button
                    }

                    val precioFormateado = "$${"%,d".format(java.util.Locale.US, precioNumero).replace(",", ".")}"

                    val medicamento = Medicamento(
                        id = medicamentoEditar?.id ?: 0,
                        nombre = nombreLimpio,
                        descripcion = descripcion.trim(),
                        precio = precioFormateado,
                        calificacion = medicamentoEditar?.calificacion ?: "4.8",
                        enOferta = enOferta,
                        imagenEmoji = medicamentoEditar?.imagenEmoji ?: "",
                        imagenRes = medicamentoEditar?.imagenRes,
                        categoria = categoria
                    )
                    alGuardar(medicamento)
                }
            ) {
                Text(text = "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = alCancelar) {
                Text(text = "Cancelar")
            }
        }
    )
}
