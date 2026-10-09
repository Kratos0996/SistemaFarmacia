package com.example.sistemafarmacia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sistemafarmacia.ui.theme.SistemaFarmaciaTheme

// 1. Estructura de datos para los medicamentos
data class Medicamento(
    val nombre: String,
    val descripcion: String,
    val precio: String
)

// 2. Colección de datos con la información a mostrar
val listaMedicamentos = listOf(
    Medicamento("Paracetamol 500mg", "Analgésico y antipirético para aliviar el dolor y la fiebre.", "$4.500"),
    Medicamento("Ibuprofeno 800mg", "Antiinflamatorio indicado para dolores fuertes e inflamación.", "$8.200"),
    Medicamento("Amoxicilina 500mg", "Antibiótico para combatir infecciones bacterianas.", "$15.900"),
    Medicamento("Omeprazol 20mg", "Tratamiento para la acidez estomacal y gastritis.", "$11.300"),
    Medicamento("Loratadina 10mg", "Antihistamínico para el alivio de alergias y rinitis.", "$6.800")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SistemaFarmaciaTheme {
                PharmacyApp()
            }
        }
    }
}

@Composable
fun PharmacyApp() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "GestorPharma",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Encuentra todos los medicamentos que necesitas",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Nombre del medicamento") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Medicamentos disponibles",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 3. Recorremos la colección utilizando un bucle for para generar las Cards
        for (medicamento in listaMedicamentos) {
            ProductCard(medicamento = medicamento)
        }
    }
}

// 4. Componente reutilizable para cada Card
@Composable
fun ProductCard(medicamento: Medicamento) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = medicamento.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Descripción: ${medicamento.descripcion}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Precio: ${medicamento.precio}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PharmacyAppPreview() {
    SistemaFarmaciaTheme {
        PharmacyApp()
    }
}
