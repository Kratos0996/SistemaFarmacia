package com.example.sistemafarmacia.data

import com.example.sistemafarmacia.model.Medicamento

object DatosIniciales {
    val medicamentos = listOf(
        Medicamento(
            id = 1,
            nombre = "Paracetamol 500mg",
            descripcion = "Analgésico y antipirético para aliviar el dolor y la fiebre.",
            precio = "$4.500",
            calificacion = "4.8",
            enOferta = true,
            imagenEmoji = "",
            categoria = "Medicamentos"
        ),
        Medicamento(
            id = 2,
            nombre = "Vitamina C 1000mg",
            descripcion = "Suplemento vitamínico para reforzar el sistema inmune.",
            precio = "$8.200",
            calificacion = "4.6",
            enOferta = false,
            imagenEmoji = "",
            categoria = "Vitaminas"
        ),
        Medicamento(
            id = 3,
            nombre = "Alcohol en Gel 250ml",
            descripcion = "Desinfectante para manos de uso diario.",
            precio = "$3.900",
            calificacion = "4.9",
            enOferta = true,
            imagenEmoji = "",
            categoria = "Cuidado Personal"
        ),
        Medicamento(
            id = 4,
            nombre = "Pañales Etapa 1",
            descripcion = "Pañales ultra absorbentes para recién nacidos.",
            precio = "$18.300",
            calificacion = "4.7",
            enOferta = false,
            imagenEmoji = "",
            categoria = "Bebé y Maternidad"
        ),
        Medicamento(
            id = 5,
            nombre = "Protector Solar FPS 50",
            descripcion = "Protección solar facial de alta eficacia.",
            precio = "$14.800",
            calificacion = "4.5",
            enOferta = true,
            imagenEmoji = "",
            categoria = "Dermocosmética"
        ),
        Medicamento(
            id = 6,
            nombre = "Ibuprofeno 400mg",
            descripcion = "Antiinflamatorio y analgésico para el alivio de dolores articulares.",
            precio = "$6.200",
            calificacion = "4.7",
            enOferta = false,
            imagenEmoji = "",
            categoria = "Medicamentos"
        ),
        Medicamento(
            id = 7,
            nombre = "Multivitamínico Completo",
            descripcion = "Complejo B, zinc y minerales para la energía diaria.",
            precio = "$12.900",
            calificacion = "4.9",
            enOferta = true,
            imagenEmoji = "",
            categoria = "Vitaminas"
        ),
        Medicamento(
            id = 8,
            nombre = "Jabón Neutro Hipoalergénico",
            descripcion = "Limpieza suave para pieles sensibles y delicadas.",
            precio = "$5.400",
            calificacion = "4.6",
            enOferta = false,
            imagenEmoji = "",
            categoria = "Cuidado Personal"
        ),
        Medicamento(
            id = 9,
            nombre = "Fórmula Láctea Maternidad 1",
            descripcion = "Nutrición completa enriquecida con DHA e Hierro para lactantes.",
            precio = "$24.500",
            calificacion = "4.8",
            enOferta = true,
            imagenEmoji = "",
            categoria = "Bebé y Maternidad"
        ),
        Medicamento(
            id = 10,
            nombre = "Sérum Ácido Hialurónico",
            descripcion = "Hidratante facial intensivo para prevenir líneas de expresión.",
            precio = "$19.900",
            calificacion = "4.9",
            enOferta = false,
            imagenEmoji = "",
            categoria = "Dermocosmética"
        )
    )
}
