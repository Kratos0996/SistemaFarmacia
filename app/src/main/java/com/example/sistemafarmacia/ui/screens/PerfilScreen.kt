package com.example.sistemafarmacia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sistemafarmacia.model.Pedido
import com.example.sistemafarmacia.model.Usuario

@Composable
fun PerfilScreen(
    usuarioActual: Usuario?,
    historialPedidos: List<Pedido>,
    alActualizarDireccion: (String) -> Unit,
    alCerrarSesion: () -> Unit
) {
    var mostrarDireccion by rememberSaveable { mutableStateOf(false) }
    var mostrarPedidos by rememberSaveable { mutableStateOf(false) }
    var mostrarCuenta by rememberSaveable { mutableStateOf(false) }
    var nuevaDireccion by rememberSaveable { mutableStateOf(usuarioActual?.direccionEnvio ?: "") }
    var direccionInvalida by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(45.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = usuarioActual?.nombreCompleto ?: "Administrador GestorPharma",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = usuarioActual?.correo ?: "admin@gestorpharma.com",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Rol: ${usuarioActual?.rol ?: "Administrador"}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${historialPedidos.size} Pedidos",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "En tu historial",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = usuarioActual?.nombreUsuario ?: "", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Usuario activo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Mi Cuenta",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OpcionPerfil(
            icono = Icons.AutoMirrored.Filled.ReceiptLong,
            titulo = "Historial de Compras",
            subtitulo = "Ver ${historialPedidos.size} pedidos anteriores",
            alSeleccionar = { mostrarPedidos = true }
        )

        OpcionPerfil(
            icono = Icons.Default.LocationOn,
            titulo = "Dirección de Envío",
            subtitulo = usuarioActual?.direccionEnvio ?: "Calle 12 # 34 - 56, Barrio Centro",
            alSeleccionar = {
                nuevaDireccion = usuarioActual?.direccionEnvio ?: "Calle 12 # 34 - 56, Barrio Centro"
                direccionInvalida = false
                mostrarDireccion = true
            }
        )

        OpcionPerfil(
            icono = Icons.Default.Settings,
            titulo = "Configuración de la Cuenta",
            subtitulo = "Privacidad y seguridad",
            alSeleccionar = { mostrarCuenta = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = alCerrarSesion,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerrar Sesión",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (mostrarDireccion) {
        AlertDialog(
            onDismissRequest = { mostrarDireccion = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = { Text("Modificar Dirección de Envío") },
            text = {
                Column {
                    Text(
                        text = "Ingresa la nueva dirección de entrega para tus pedidos:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nuevaDireccion,
                        onValueChange = { nuevaDireccion = it; direccionInvalida = false },
                        label = { Text("Dirección de Envío") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = direccionInvalida,
                        supportingText = if (direccionInvalida) { { Text("Ingresa una dirección de envío") } } else null,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevaDireccion.isNotBlank()) {
                            alActualizarDireccion(nuevaDireccion.trim())
                            mostrarDireccion = false
                        } else {
                            direccionInvalida = true
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDireccion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarPedidos) {
        AlertDialog(
            onDismissRequest = { mostrarPedidos = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = { Text("Historial de Compras") },
            text = {
                if (historialPedidos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aún no has realizado pedidos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(historialPedidos) { pedido ->
                            TarjetaPedido(pedido = pedido)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarPedidos = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (mostrarCuenta) {
        AlertDialog(
            onDismissRequest = { mostrarCuenta = false },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            title = { Text("Privacidad y seguridad") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Usuario: ${usuarioActual?.nombreUsuario.orEmpty()}")
                    Text("La cuenta de demostración funciona sin conexión. La contraseña se verifica de forma segura en el dispositivo.")
                    Text("La sesión termina al cerrar sesión o al iniciar un proceso nuevo de la aplicación.")
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarCuenta = false }) { Text("Cerrar") }
            }
        )
    }
}

@Composable
private fun OpcionPerfil(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    alSeleccionar: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { alSeleccionar() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = titulo, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Text(text = subtitulo, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun TarjetaPedido(pedido: Pedido) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pedido #${pedido.id}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = pedido.fecha,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = pedido.resumenProductos,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Envío: ${pedido.direccionEnvio}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = pedido.total,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
